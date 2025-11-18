#!/bin/bash

# Service Verification Script
# This script checks if all services are running and healthy

echo "=========================================="
echo "Urban Transport System - Service Verification"
echo "=========================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to check service health
check_service() {
    local service_name=$1
    local port=$2
    local url="http://localhost:${port}/actuator/health"
    
    response=$(curl -s -o /dev/null -w "%{http_code}" $url 2>/dev/null)
    
    if [ "$response" = "200" ]; then
        status=$(curl -s $url | grep -o '"status":"[^"]*"' | cut -d'"' -f4)
        if [ "$status" = "UP" ]; then
            echo -e "${GREEN}✓${NC} $service_name (port $port) - ${GREEN}UP${NC}"
            return 0
        else
            echo -e "${RED}✗${NC} $service_name (port $port) - ${RED}$status${NC}"
            return 1
        fi
    else
        echo -e "${RED}✗${NC} $service_name (port $port) - ${RED}NOT RESPONDING${NC}"
        return 1
    fi
}

# Check Docker Compose
echo "1. Checking Docker Compose status..."
if docker-compose ps > /dev/null 2>&1; then
    echo -e "${GREEN}✓${NC} Docker Compose is running"
else
    echo -e "${RED}✗${NC} Docker Compose is not running"
    exit 1
fi

echo ""
echo "2. Checking container status..."
docker-compose ps

echo ""
echo "3. Checking service health endpoints..."

# Check all services
services=(
    "service-registry:8761"
    "config-server:8888"
    "user-service:8081"
    "ticket-service:8082"
    "schedule-service:8083"
    "geolocation-service:8084"
    "subscription-service:8085"
    "api-gateway:8080"
)

failed=0
for service in "${services[@]}"; do
    IFS=':' read -r name port <<< "$service"
    if ! check_service "$name" "$port"; then
        ((failed++))
    fi
    sleep 0.5
done

echo ""
echo "4. Checking databases..."

# Check PostgreSQL databases
pg_databases=("postgres-user:user_db" "postgres-schedule:schedule_db" "postgres-subscription:subscription_service" "postgres-ticket:ticket-service-db")

for db in "${pg_databases[@]}"; do
    IFS=':' read -r container dbname <<< "$db"
    if docker exec $container pg_isready -U hiba -d $dbname > /dev/null 2>&1; then
        echo -e "${GREEN}✓${NC} PostgreSQL: $container/$dbname - ${GREEN}READY${NC}"
    else
        echo -e "${RED}✗${NC} PostgreSQL: $container/$dbname - ${RED}NOT READY${NC}"
        ((failed++))
    fi
done

# Check MongoDB
if docker exec mongodb mongosh -u hiba -p hiba --authenticationDatabase admin --eval "db.adminCommand('ping')" > /dev/null 2>&1; then
    echo -e "${GREEN}✓${NC} MongoDB: geolocation_db - ${GREEN}READY${NC}"
else
    echo -e "${RED}✗${NC} MongoDB: geolocation_db - ${RED}NOT READY${NC}"
    ((failed++))
fi

# Check Redis
if docker exec redis redis-cli ping > /dev/null 2>&1; then
    echo -e "${GREEN}✓${NC} Redis - ${GREEN}READY${NC}"
else
    echo -e "${RED}✗${NC} Redis - ${RED}NOT READY${NC}"
    ((failed++))
fi

# Check Kafka
if docker exec kafka kafka-broker-api-versions --bootstrap-server localhost:9092 > /dev/null 2>&1; then
    echo -e "${GREEN}✓${NC} Kafka - ${GREEN}READY${NC}"
else
    echo -e "${YELLOW}⚠${NC} Kafka - ${YELLOW}CHECKING...${NC}"
fi

echo ""
echo "5. Checking Eureka Registry..."
eureka_response=$(curl -s -u eureka:eureka123 http://localhost:8761/eureka/apps 2>/dev/null)
if [ -n "$eureka_response" ]; then
    registered_services=$(echo "$eureka_response" | grep -o '<name>[^<]*</name>' | sed 's/<name>//;s/<\/name>//' | sort -u)
    if [ -n "$registered_services" ]; then
        echo -e "${GREEN}✓${NC} Eureka is accessible"
        echo "   Registered services:"
        echo "$registered_services" | while read service; do
            echo "   - $service"
        done
    else
        echo -e "${YELLOW}⚠${NC} Eureka is accessible but no services registered yet"
    fi
else
    echo -e "${RED}✗${NC} Cannot access Eureka"
    ((failed++))
fi

echo ""
echo "=========================================="
if [ $failed -eq 0 ]; then
    echo -e "${GREEN}All services are healthy!${NC}"
    exit 0
else
    echo -e "${RED}$failed service(s) have issues${NC}"
    echo ""
    echo "To view logs:"
    echo "  docker-compose logs <service-name>"
    echo ""
    echo "To restart a service:"
    echo "  docker-compose restart <service-name>"
    exit 1
fi

