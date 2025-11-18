# Multi-stage build for geolocation-service
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy service pom.xml (geolocation-service is standalone)
COPY geolocation-service/pom.xml ./geolocation-service/pom.xml

# Download dependencies
RUN mvn dependency:go-offline -B -f geolocation-service/pom.xml

# Copy source code
COPY geolocation-service/src ./geolocation-service/src

# Build the application
RUN mvn clean package -DskipTests -B -f geolocation-service/pom.xml

# Verify the JAR was created
RUN test -f /app/geolocation-service/target/geolocation-service-*.jar || (echo "JAR file not found!" && exit 1)

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Install curl for health checks
RUN apk add --no-cache curl tzdata && rm -rf /var/cache/apk/*

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring

# Copy jar from build stage
COPY --from=build --chown=spring:spring /app/geolocation-service/target/geolocation-service-*.jar app.jar

# Switch to non-root user
USER spring:spring

# Expose port
EXPOSE 8080

# JVM optimizations
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+UseStringDeduplication"

# Health check
HEALTHCHECK --interval=30s --timeout=5s --retries=3 --start-period=60s \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

