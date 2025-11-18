# Quick Start Guide

## Fastest Way to Run Everything

### 1. Build and Start All Services
```bash
docker-compose up --build -d
```

### 2. Wait for Services to Start (2-3 minutes)
```bash
# Watch logs
docker-compose logs -f

# Or check status
docker-compose ps
```

### 3. Verify Everything is Running

**Option A: Use Verification Script**
```bash
# Linux/Mac
./verify-services.sh

# Windows PowerShell
.\verify-services.ps1
```

**Option B: Manual Check**
```bash
# Check all containers
docker-compose ps

# Check health endpoints
curl http://localhost:8080/actuator/health  # API Gateway
curl http://localhost:8081/actuator/health  # User Service
curl http://localhost:8082/actuator/health  # Ticket Service
curl http://localhost:8083/actuator/health  # Schedule Service
curl http://localhost:8084/actuator/health  # Geolocation Service
curl http://localhost:8085/actuator/health  # Subscription Service
curl http://localhost:8761/actuator/health # Eureka
```

### 4. Access Services

- **Eureka Dashboard:** http://localhost:8761 (eureka/eureka123)
- **API Gateway:** http://localhost:8080
- **User Service:** http://localhost:8081
- **Ticket Service:** http://localhost:8082
- **Schedule Service:** http://localhost:8083
- **Geolocation Service:** http://localhost:8084
- **Subscription Service:** http://localhost:8085

## Common Commands

```bash
# Stop everything
docker-compose down

# Stop and remove volumes (clean slate)
docker-compose down -v

# View logs
docker-compose logs -f <service-name>

# Restart a service
docker-compose restart <service-name>

# Rebuild a specific service
docker-compose build <service-name>
docker-compose up -d <service-name>
```

## Troubleshooting

If a service fails:
1. Check logs: `docker-compose logs <service-name>`
2. Check if dependencies are healthy: `docker-compose ps`
3. Restart: `docker-compose restart <service-name>`

For detailed troubleshooting, see `DOCKER_SETUP_GUIDE.md`

