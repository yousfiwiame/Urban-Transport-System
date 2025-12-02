# Multi-stage build for subscription-service
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy parent pom.xml first
COPY pom.xml ./pom.xml

# Create minimal pom.xml files for other modules to satisfy parent pom validation
RUN mkdir -p service-registry config-server api-gateway user-service ticket-service geolocation-service schedule-service notification-service && \
    echo '<?xml version="1.0" encoding="UTF-8"?><project xmlns="http://maven.apache.org/POM/4.0.0"><modelVersion>4.0.0</modelVersion><groupId>com.transport.urbain</groupId><artifactId>service-registry</artifactId><version>1.0.0</version><packaging>pom</packaging></project>' > service-registry/pom.xml && \
    echo '<?xml version="1.0" encoding="UTF-8"?><project xmlns="http://maven.apache.org/POM/4.0.0"><modelVersion>4.0.0</modelVersion><groupId>com.transport.urbain</groupId><artifactId>config-server</artifactId><version>1.0.0</version><packaging>pom</packaging></project>' > config-server/pom.xml && \
    echo '<?xml version="1.0" encoding="UTF-8"?><project xmlns="http://maven.apache.org/POM/4.0.0"><modelVersion>4.0.0</modelVersion><groupId>com.transport.urbain</groupId><artifactId>api-gateway</artifactId><version>1.0.0</version><packaging>pom</packaging></project>' > api-gateway/pom.xml && \
    echo '<?xml version="1.0" encoding="UTF-8"?><project xmlns="http://maven.apache.org/POM/4.0.0"><modelVersion>4.0.0</modelVersion><groupId>com.transport.urbain</groupId><artifactId>user-service</artifactId><version>1.0.0</version><packaging>pom</packaging></project>' > user-service/pom.xml && \
    echo '<?xml version="1.0" encoding="UTF-8"?><project xmlns="http://maven.apache.org/POM/4.0.0"><modelVersion>4.0.0</modelVersion><groupId>com.transport.urbain</groupId><artifactId>ticket-service</artifactId><version>1.0.0</version><packaging>pom</packaging></project>' > ticket-service/pom.xml && \
    echo '<?xml version="1.0" encoding="UTF-8"?><project xmlns="http://maven.apache.org/POM/4.0.0"><modelVersion>4.0.0</modelVersion><groupId>com.transport.urbain</groupId><artifactId>geolocation-service</artifactId><version>1.0.0</version><packaging>pom</packaging></project>' > geolocation-service/pom.xml && \
    echo '<?xml version="1.0" encoding="UTF-8"?><project xmlns="http://maven.apache.org/POM/4.0.0"><modelVersion>4.0.0</modelVersion><groupId>com.transport.urbain</groupId><artifactId>schedule-service</artifactId><version>1.0.0</version><packaging>pom</packaging></project>' > schedule-service/pom.xml && \
    echo '<?xml version="1.0" encoding="UTF-8"?><project xmlns="http://maven.apache.org/POM/4.0.0"><modelVersion>4.0.0</modelVersion><groupId>com.transport.urbain</groupId><artifactId>notification-service</artifactId><version>1.0.0</version><packaging>pom</packaging></project>' > notification-service/pom.xml

# Copy subscription-service pom.xml and source
COPY subscription-service/pom.xml ./subscription-service/pom.xml
COPY subscription-service/src ./subscription-service/src

# Download dependencies
RUN mvn dependency:go-offline -B -f pom.xml -N || true
RUN mvn dependency:go-offline -B -f subscription-service/pom.xml

# Build the application
RUN set -e && mvn clean package -DskipTests -B -f subscription-service/pom.xml

# Verify the JAR was created
RUN JAR_FILE=$(find /app/subscription-service/target -name "subscription-service*.jar" -not -name "*-sources.jar" -not -name "*-javadoc.jar" -type f | head -n 1) && \
    if [ -z "$JAR_FILE" ]; then \
        echo "JAR file not found! Contents of target directory:"; \
        ls -la /app/subscription-service/target/ || echo "Target directory does not exist"; \
        exit 1; \
    else \
        echo "Found JAR: $JAR_FILE"; \
        if [ "$JAR_FILE" != "/app/subscription-service/target/subscription-service.jar" ]; then \
            echo "Copying $JAR_FILE to subscription-service.jar"; \
            cp "$JAR_FILE" /app/subscription-service/target/subscription-service.jar; \
        else \
            echo "JAR already has the correct name: subscription-service.jar"; \
        fi; \
    fi

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Install curl for health checks
RUN apk add --no-cache curl tzdata && rm -rf /var/cache/apk/*

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring

# Copy jar from build stage
COPY --from=build --chown=spring:spring /app/subscription-service/target/subscription-service.jar app.jar

# Switch to non-root user
USER spring:spring

# Expose port
EXPOSE 8084

# JVM optimizations
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+UseStringDeduplication"

# Health check
HEALTHCHECK --interval=30s --timeout=5s --retries=3 --start-period=60s \
    CMD curl -f http://localhost:8084/actuator/health || exit 1

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
