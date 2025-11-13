# Multi-stage build for subscription-service
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml and download dependencies
COPY backend/pom.xml .
COPY backend/subscription-service/pom.xml ./subscription-service/

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY backend/subscription-service ./subscription-service/

# Build the application
RUN mvn clean package -DskipTests -B -f subscription-service/pom.xml

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy jar from build stage
COPY --from=build /app/subscription-service/target/subscription-service-*.jar app.jar

# Expose port
EXPOSE 8085

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8085/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

