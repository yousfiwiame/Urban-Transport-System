# Multi-stage build for user-service
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy parent pom.xml first
COPY pom.xml ./pom.xml

# Create minimal pom.xml files for other modules to satisfy parent pom validation
RUN mkdir -p service-registry config-server api-gateway schedule-service subscription-service && \
    echo '<?xml version="1.0" encoding="UTF-8"?><project xmlns="http://maven.apache.org/POM/4.0.0"><modelVersion>4.0.0</modelVersion><groupId>com.transport.urbain</groupId><artifactId>service-registry</artifactId><version>1.0.0</version><packaging>pom</packaging></project>' > service-registry/pom.xml && \
    echo '<?xml version="1.0" encoding="UTF-8"?><project xmlns="http://maven.apache.org/POM/4.0.0"><modelVersion>4.0.0</modelVersion><groupId>com.transport.urbain</groupId><artifactId>config-server</artifactId><version>1.0.0</version><packaging>pom</packaging></project>' > config-server/pom.xml && \
    echo '<?xml version="1.0" encoding="UTF-8"?><project xmlns="http://maven.apache.org/POM/4.0.0"><modelVersion>4.0.0</modelVersion><groupId>com.transport.urbain</groupId><artifactId>api-gateway</artifactId><version>1.0.0</version><packaging>pom</packaging></project>' > api-gateway/pom.xml && \
    echo '<?xml version="1.0" encoding="UTF-8"?><project xmlns="http://maven.apache.org/POM/4.0.0"><modelVersion>4.0.0</modelVersion><groupId>com.transport.urbain</groupId><artifactId>schedule-service</artifactId><version>1.0.0</version><packaging>pom</packaging></project>' > schedule-service/pom.xml && \
    echo '<?xml version="1.0" encoding="UTF-8"?><project xmlns="http://maven.apache.org/POM/4.0.0"><modelVersion>4.0.0</modelVersion><groupId>com.transport.urbain</groupId><artifactId>subscription-service</artifactId><version>1.0.0</version><packaging>pom</packaging></project>' > subscription-service/pom.xml

# Copy user-service pom.xml and source
COPY user-service/pom.xml ./user-service/pom.xml
COPY user-service/src ./user-service/src

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B -f pom.xml -N || true
RUN mvn dependency:go-offline -B -f user-service/pom.xml

# Build the application
RUN mvn clean package -DskipTests -B -f user-service/pom.xml

# Verify the JAR was created
RUN test -f /app/user-service/target/user-service-*.jar || (echo "JAR file not found!" && exit 1)

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Install curl for health checks
RUN apk add --no-cache curl tzdata && rm -rf /var/cache/apk/*

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring

# Copy jar from build stage
COPY --from=build --chown=spring:spring /app/user-service/target/user-service-*.jar app.jar

# Switch to non-root user
USER spring:spring

# Expose port
EXPOSE 8081

# JVM optimizations
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+UseStringDeduplication"

# Health check
HEALTHCHECK --interval=30s --timeout=5s --retries=3 --start-period=60s \
    CMD curl -f http://localhost:8081/actuator/health || exit 1

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
