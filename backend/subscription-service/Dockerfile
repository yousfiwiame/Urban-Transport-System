# Multi-stage build for subscription-service
# Build context should be from backend/ directory: docker build -f subscription-service/Dockerfile -t subscription-service:latest .

# ============================================
# Stage 1: Build
# ============================================
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy parent pom.xml first for dependency resolution
COPY pom.xml ./pom.xml

# Copy service pom.xml
COPY subscription-service/pom.xml ./subscription-service/pom.xml

# Download dependencies (this layer will be cached if pom.xml doesn't change)
# This significantly speeds up subsequent builds
RUN mvn dependency:go-offline -B -f pom.xml || true
RUN mvn dependency:go-offline -B -f subscription-service/pom.xml

# Copy source code
COPY subscription-service/src ./subscription-service/src

# Build the application (skip tests for faster build, run tests in CI/CD)
RUN mvn clean package -DskipTests -B -f subscription-service/pom.xml

# Verify the JAR was created
RUN test -f /app/subscription-service/target/subscription-service-*.jar || (echo "JAR file not found!" && exit 1)

# ============================================
# Stage 2: Runtime
# ============================================
FROM eclipse-temurin:17-jre-alpine

# Metadata
LABEL maintainer="Urban Transport System"
LABEL description="Subscription Service Microservice"
LABEL version="1.0"

WORKDIR /app

# Install curl for health checks and bash for debugging
RUN apk add --no-cache \
    curl \
    bash \
    tzdata \
    && rm -rf /var/cache/apk/*

# Set timezone (optional, adjust as needed)
ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Create non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring

# Copy jar from build stage
COPY --from=build --chown=spring:spring /app/subscription-service/target/subscription-service-*.jar app.jar

# Switch to non-root user
USER spring:spring

# Expose port
EXPOSE 8085

# Health check configuration
HEALTHCHECK --interval=30s \
            --timeout=5s \
            --start-period=60s \
            --retries=3 \
            CMD curl -f http://localhost:8085/actuator/health || exit 1

# JVM optimizations for container
# - UseContainerSupport: Automatically detect container memory limits
# - MaxRAMPercentage: Use 75% of available memory for heap
# - UseG1GC: Use G1 garbage collector (good for low latency)
# - UseStringDeduplication: Reduce memory usage
# - AlwaysPreTouch: Pre-allocate memory pages
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:+UseG1GC \
               -XX:+UseStringDeduplication \
               -XX:+AlwaysPreTouch \
               -Djava.security.egd=file:/dev/./urandom"

# Spring Boot specific optimizations
ENV SPRING_OUTPUT_ANSI_ENABLED=always

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
