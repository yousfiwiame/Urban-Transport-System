# Docker Setup and Execution Guide

## Prerequisites

Before starting, ensure you have:
- Docker Desktop installed and running (or Docker Engine + Docker Compose)
- At least 8GB of RAM available
- At least 10GB of free disk space
- Ports available: 8080, 8081, 8082, 8083, 8084, 8085, 8761, 8888, 5432-5435, 27017, 6379, 9092, 2181

## Step-by-Step Execution

### Step 1: Verify Docker Installation

**Command:**
```bash
docker --version
docker-compose --version
```

**What to watch for:**
- Docker version should be 20.10 or higher
- Docker Compose version should be 1.29 or higher (or v2.x)
- No error messages

**If errors occur:**
- Start Docker Desktop if on Windows/Mac
- Check Docker service: `sudo systemctl status docker` (Linux)

---

### Step 2: Navigate to Project Root

**Command:**
```bash
cd "C:\Users\DELL\Downloads\Urban-Transport-System-main\Urban-Transport-System-main"
```

**What to watch for:**
- You should be in the directory containing `docker-compose.yml` and `backend/` folder
- Verify with: `ls docker-compose.yml` (Linux/Mac) or `dir docker-compose.yml` (Windows)

---

### Step 3: Check Docker Compose File

**Command:**
```bash
docker-compose config
```

**What to watch for:**
- No syntax errors
- All services are listed: service-registry, config-server, postgres-user, postgres-schedule, postgres-subscription, postgres-ticket, mongodb, user-service, schedule-service, subscription-service, ticket-service, geolocation-service, api-gateway, redis, zookeeper, kafka
- No "ERROR" messages

**If errors occur:**
- Check YAML indentation (must be spaces, not tabs)
- Verify all paths in docker-compose.yml are correct

---

### Step 4: Stop Any Running Containers (Optional but Recommended)

**Command:**
```bash
docker-compose down
docker ps -a
```

**What to watch for:**
- All containers should be stopped
- `docker ps -a` should show no containers (or only unrelated ones)

---

### Step 5: Build All Docker Images

**Command:**
```bash
docker-compose build --no-cache
```

**What to watch for:**
- This will take 10-20 minutes on first run
- You should see:
  ```
  Building service-registry...
  Building config-server...
  Building user-service...
  Building schedule-service...
  Building subscription-service...
  Building ticket-service...
  Building geolocation-service...
  Building api-gateway...
  ```
- Each service should show:
  - `Step 1/X : FROM maven:3.9.6-eclipse-temurin-17 AS build`
  - `Step X/X : FROM eclipse-temurin:17-jre-alpine`
  - `Successfully built <image-id>`
  - `Successfully tagged <service-name>:latest`

**Common issues:**
- **Maven download errors**: Check internet connection, retry
- **Out of memory**: Close other applications, increase Docker memory limit
- **Build fails on COPY**: Verify file paths in Dockerfiles

**Expected output at end:**
```
Successfully built <image-id>
Successfully tagged service-registry:latest
Successfully built <image-id>
Successfully tagged config-server:latest
... (for all services)
```

---

### Step 6: Start Infrastructure Services First

**Command:**
```bash
docker-compose up -d service-registry postgres-user postgres-schedule postgres-subscription postgres-ticket mongodb redis zookeeper kafka
```

**What to watch for:**
- All containers should start: `Creating postgres-user...`, `Creating mongodb...`, etc.
- Check status: `docker-compose ps`

**Verify each service:**

1. **Service Registry (Eureka):**
   ```bash
   docker-compose logs service-registry
   ```
   - Look for: `Started ServiceRegistryApplication`
   - Open browser: http://localhost:8761
   - Should see Eureka dashboard (may need username: eureka, password: eureka123)

2. **PostgreSQL Databases:**
   ```bash
   docker-compose logs postgres-user
   docker-compose logs postgres-schedule
   docker-compose logs postgres-subscription
   docker-compose logs postgres-ticket
   ```
   - Look for: `database system is ready to accept connections`
   - Check health: `docker-compose ps` - should show "healthy" status

3. **MongoDB:**
   ```bash
   docker-compose logs mongodb
   ```
   - Look for: `Waiting for connections` or `MongoDB init process complete`
   - Verify: `docker exec -it mongodb mongosh -u hiba -p hiba --authenticationDatabase admin`

4. **Redis:**
   ```bash
   docker-compose logs redis
   ```
   - Look for: `Ready to accept connections`

5. **Zookeeper:**
   ```bash
   docker-compose logs zookeeper
   ```
   - Look for: `binding to port 0.0.0.0/0.0.0.0:2181`

6. **Kafka:**
   ```bash
   docker-compose logs kafka
   ```
   - Look for: `started (kafka.server.KafkaServer)`
   - Wait 30-60 seconds for Kafka to fully start

**Health check command:**
```bash
docker-compose ps
```
- All services should show "Up" and "healthy" status
- No "unhealthy" or "restarting" status

---

### Step 7: Start Config Server

**Command:**
```bash
docker-compose up -d config-server
```

**What to watch for:**
```bash
docker-compose logs config-server
```
- Look for: `Started ConfigServerApplication`
- Check: http://localhost:8888/actuator/health
- Should return: `{"status":"UP"}`

**Verify:**
```bash
curl http://localhost:8888/actuator/health
```
- Should return JSON with status "UP"

---

### Step 8: Start Application Services

**Command:**
```bash
docker-compose up -d user-service schedule-service subscription-service ticket-service geolocation-service
```

**What to watch for:**

1. **User Service:**
   ```bash
   docker-compose logs user-service
   ```
   - Look for: `Started UserServiceApplication`
   - Look for: `HikariPool-1 - Starting...` then `HikariPool-1 - Start completed`
   - Check: http://localhost:8081/actuator/health
   - Should show: `{"status":"UP"}`

2. **Schedule Service:**
   ```bash
   docker-compose logs schedule-service
   ```
   - Look for: `Started ScheduleServiceApplication`
   - Database connection successful
   - Check: http://localhost:8083/actuator/health

3. **Subscription Service:**
   ```bash
   docker-compose logs subscription-service
   ```
   - Look for: `Started SubscriptionServiceApplication`
   - Check: http://localhost:8085/actuator/health

4. **Ticket Service:**
   ```bash
   docker-compose logs ticket-service
   ```
   - Look for: `Started TicketServiceApplication`
   - Check: http://localhost:8082/actuator/health

5. **Geolocation Service:**
   ```bash
   docker-compose logs geolocation-service
   ```
   - Look for: `Started GeolocationServiceApplication`
   - MongoDB connection successful
   - Check: http://localhost:8084/actuator/health

**Verify all services:**
```bash
docker-compose ps
```
- All application services should be "Up" and "healthy"
- Check Eureka dashboard: http://localhost:8761
- Services should appear in "Instances currently registered with Eureka"

---

### Step 9: Start API Gateway (Last)

**Command:**
```bash
docker-compose up -d api-gateway
```

**What to watch for:**
```bash
docker-compose logs api-gateway
```
- Look for: `Started ApiGatewayApplication`
- Look for: Routes being registered
- Check: http://localhost:8080/actuator/health
- Check: http://localhost:8080/actuator/gateway/routes (should list all routes)

---

### Step 10: Final Verification

**1. Check All Containers:**
```bash
docker-compose ps
```

**Expected output:**
```
NAME                      STATUS          PORTS
api-gateway              Up (healthy)    0.0.0.0:8080->8080/tcp
config-server            Up (healthy)    0.0.0.0:8888->8888/tcp
geolocation-service      Up (healthy)    0.0.0.0:8084->8080/tcp
kafka                    Up (healthy)    0.0.0.0:9092->9092/tcp
mongodb                  Up (healthy)    0.0.0.0:27017->27017/tcp
postgres-schedule        Up (healthy)    0.0.0.0:5433->5432/tcp
postgres-subscription    Up (healthy)    0.0.0.0:5434->5432/tcp
postgres-ticket          Up (healthy)    0.0.0.0:5435->5432/tcp
postgres-user            Up (healthy)    0.0.0.0:5432->5432/tcp
redis                    Up (healthy)    0.0.0.0:6379->6379/tcp
schedule-service         Up (healthy)    0.0.0.0:8083->8083/tcp
service-registry         Up (healthy)    0.0.0.0:8761->8761/tcp
subscription-service     Up (healthy)    0.0.0.0:8085->8085/tcp
ticket-service           Up (healthy)    0.0.0.0:8082->8082/tcp
user-service             Up (healthy)    0.0.0.0:8081->8081/tcp
zookeeper                Up              2181/tcp
```

**2. Check Service Health Endpoints:**

```bash
# Service Registry
curl http://localhost:8761/actuator/health

# Config Server
curl http://localhost:8888/actuator/health

# User Service
curl http://localhost:8081/actuator/health

# Schedule Service
curl http://localhost:8083/actuator/health

# Subscription Service
curl http://localhost:8085/actuator/health

# Ticket Service
curl http://localhost:8082/actuator/health

# Geolocation Service
curl http://localhost:8084/actuator/health

# API Gateway
curl http://localhost:8080/actuator/health
```

**All should return:** `{"status":"UP"}`

**3. Check Eureka Dashboard:**
- Open: http://localhost:8761
- Login: username: `eureka`, password: `eureka123`
- Under "Instances currently registered with Eureka", you should see:
  - API-GATEWAY
  - CONFIG-SERVER
  - USER-SERVICE
  - SCHEDULE-SERVICE
  - SUBSCRIPTION-SERVICE
  - (Ticket and Geolocation may not appear if they don't use Eureka)

**4. Check Database Connections:**

**PostgreSQL:**
```bash
docker exec -it postgres-user psql -U hiba -d user_db -c "\dt"
docker exec -it postgres-schedule psql -U hiba -d schedule_db -c "\dt"
docker exec -it postgres-subscription psql -U hiba -d subscription_service -c "\dt"
docker exec -it postgres-ticket psql -U hiba -d ticket-service-db -c "\dt"
```

**MongoDB:**
```bash
docker exec -it mongodb mongosh -u hiba -p hiba --authenticationDatabase admin --eval "show dbs"
```

**5. Test API Gateway Routes:**
```bash
curl http://localhost:8080/actuator/gateway/routes
```

**6. Check Logs for Errors:**
```bash
docker-compose logs | grep -i error
docker-compose logs | grep -i exception
docker-compose logs | grep -i failed
```

**What to watch for:**
- No critical errors
- No connection refused errors
- No database connection failures
- Services should show "Started" messages

---

## Quick Start (All at Once)

If you want to start everything at once:

```bash
# Build all images
docker-compose build

# Start all services
docker-compose up -d

# Watch logs
docker-compose logs -f
```

**What to watch for:**
- All services start in order (infrastructure first, then applications)
- Wait 2-3 minutes for everything to initialize
- Check `docker-compose ps` - all should be healthy

---

## Useful Commands

### View Logs
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f user-service

# Last 100 lines
docker-compose logs --tail=100 user-service
```

### Restart a Service
```bash
docker-compose restart user-service
```

### Stop Everything
```bash
docker-compose down
```

### Stop and Remove Volumes (Clean Slate)
```bash
docker-compose down -v
```

### Check Resource Usage
```bash
docker stats
```

### Access Container Shell
```bash
docker exec -it user-service sh
```

---

## Troubleshooting

### Service Won't Start
1. Check logs: `docker-compose logs <service-name>`
2. Check if dependencies are healthy: `docker-compose ps`
3. Restart: `docker-compose restart <service-name>`

### Database Connection Errors
1. Verify database is healthy: `docker-compose ps`
2. Check database logs: `docker-compose logs postgres-user`
3. Verify credentials in application-docker.yml files

### Port Already in Use
1. Find what's using the port: `netstat -ano | findstr :8080` (Windows) or `lsof -i :8080` (Linux/Mac)
2. Stop the conflicting service or change port in docker-compose.yml

### Out of Memory
1. Increase Docker memory limit in Docker Desktop settings
2. Close other applications
3. Check: `docker stats`

### Build Fails
1. Clear Docker cache: `docker system prune -a`
2. Rebuild without cache: `docker-compose build --no-cache`
3. Check Dockerfile paths are correct

---

## Expected Service URLs

- **Eureka Dashboard:** http://localhost:8761
- **Config Server:** http://localhost:8888
- **API Gateway:** http://localhost:8080
- **User Service:** http://localhost:8081
- **Ticket Service:** http://localhost:8082
- **Schedule Service:** http://localhost:8083
- **Geolocation Service:** http://localhost:8084
- **Subscription Service:** http://localhost:8085

All services have `/actuator/health` endpoint for health checks.

---

## Success Indicators

✅ All containers show "Up (healthy)" in `docker-compose ps`
✅ All health endpoints return `{"status":"UP"}`
✅ Services appear in Eureka dashboard
✅ No errors in logs
✅ Database connections successful
✅ API Gateway can route to services

