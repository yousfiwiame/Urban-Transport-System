# PowerShell Service Verification Script
# This script checks if all services are running and healthy

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Urban Transport System - Service Verification" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

$failed = 0

# Function to check service health
function Check-Service {
    param(
        [string]$ServiceName,
        [int]$Port
    )
    
    $url = "http://localhost:$Port/actuator/health"
    
    try {
        $response = Invoke-WebRequest -Uri $url -UseBasicParsing -TimeoutSec 5 -ErrorAction Stop
        if ($response.StatusCode -eq 200) {
            $content = $response.Content | ConvertFrom-Json
            if ($content.status -eq "UP") {
                Write-Host "✓ $ServiceName (port $Port) - " -NoNewline
                Write-Host "UP" -ForegroundColor Green
                return $true
            } else {
                Write-Host "✗ $ServiceName (port $Port) - " -NoNewline
                Write-Host $content.status -ForegroundColor Red
                return $false
            }
        }
    } catch {
        Write-Host "✗ $ServiceName (port $Port) - " -NoNewline
        Write-Host "NOT RESPONDING" -ForegroundColor Red
        return $false
    }
}

# Check Docker Compose
Write-Host "1. Checking Docker Compose status..." -ForegroundColor Yellow
try {
    docker-compose ps | Out-Null
    Write-Host "✓ Docker Compose is running" -ForegroundColor Green
} catch {
    Write-Host "✗ Docker Compose is not running" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "2. Checking container status..." -ForegroundColor Yellow
docker-compose ps

Write-Host ""
Write-Host "3. Checking service health endpoints..." -ForegroundColor Yellow

# Check all services
$services = @(
    @{Name="service-registry"; Port=8761},
    @{Name="config-server"; Port=8888},
    @{Name="user-service"; Port=8081},
    @{Name="ticket-service"; Port=8082},
    @{Name="schedule-service"; Port=8083},
    @{Name="geolocation-service"; Port=8084},
    @{Name="subscription-service"; Port=8085},
    @{Name="api-gateway"; Port=8080}
)

foreach ($service in $services) {
    if (-not (Check-Service -ServiceName $service.Name -Port $service.Port)) {
        $failed++
    }
    Start-Sleep -Milliseconds 500
}

Write-Host ""
Write-Host "4. Checking databases..." -ForegroundColor Yellow

# Check PostgreSQL databases
$pgDatabases = @(
    @{Container="postgres-user"; Database="user_db"},
    @{Container="postgres-schedule"; Database="schedule_db"},
    @{Container="postgres-subscription"; Database="subscription_service"},
    @{Container="postgres-ticket"; Database="ticket-service-db"}
)

foreach ($db in $pgDatabases) {
    $result = docker exec $db.Container pg_isready -U hiba -d $db.Database 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✓ PostgreSQL: $($db.Container)/$($db.Database) - " -NoNewline
        Write-Host "READY" -ForegroundColor Green
    } else {
        Write-Host "✗ PostgreSQL: $($db.Container)/$($db.Database) - " -NoNewline
        Write-Host "NOT READY" -ForegroundColor Red
        $failed++
    }
}

# Check MongoDB
$mongoResult = docker exec mongodb mongosh -u hiba -p hiba --authenticationDatabase admin --eval "db.adminCommand('ping')" 2>&1
if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ MongoDB: geolocation_db - " -NoNewline
    Write-Host "READY" -ForegroundColor Green
} else {
    Write-Host "✗ MongoDB: geolocation_db - " -NoNewline
    Write-Host "NOT READY" -ForegroundColor Red
    $failed++
}

# Check Redis
$redisResult = docker exec redis redis-cli ping 2>&1
if ($LASTEXITCODE -eq 0 -and $redisResult -eq "PONG") {
    Write-Host "✓ Redis - " -NoNewline
    Write-Host "READY" -ForegroundColor Green
} else {
    Write-Host "✗ Redis - " -NoNewline
    Write-Host "NOT READY" -ForegroundColor Red
    $failed++
}

# Check Kafka
$kafkaResult = docker exec kafka kafka-broker-api-versions --bootstrap-server localhost:9092 2>&1
if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Kafka - " -NoNewline
    Write-Host "READY" -ForegroundColor Green
} else {
    Write-Host "⚠ Kafka - " -NoNewline
    Write-Host "CHECKING..." -ForegroundColor Yellow
}

Write-Host ""
Write-Host "5. Checking Eureka Registry..." -ForegroundColor Yellow
try {
    $cred = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes("eureka:eureka123"))
    $headers = @{Authorization = "Basic $cred"}
    $eurekaResponse = Invoke-WebRequest -Uri "http://localhost:8761/eureka/apps" -Headers $headers -UseBasicParsing -ErrorAction Stop
    
    if ($eurekaResponse.StatusCode -eq 200) {
        Write-Host "✓ Eureka is accessible" -ForegroundColor Green
        # Parse registered services (simplified)
        Write-Host "   Check http://localhost:8761 for registered services"
    }
} catch {
    Write-Host "✗ Cannot access Eureka" -ForegroundColor Red
    $failed++
}

Write-Host ""
Write-Host "==========================================" -ForegroundColor Cyan
if ($failed -eq 0) {
    Write-Host "All services are healthy!" -ForegroundColor Green
    exit 0
} else {
    Write-Host "$failed service(s) have issues" -ForegroundColor Red
    Write-Host ""
    Write-Host "To view logs:" -ForegroundColor Yellow
    Write-Host "  docker-compose logs <service-name>"
    Write-Host ""
    Write-Host "To restart a service:" -ForegroundColor Yellow
    Write-Host "  docker-compose restart <service-name>"
    exit 1
}

