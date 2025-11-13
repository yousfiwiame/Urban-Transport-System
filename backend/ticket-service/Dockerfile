FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the JAR file
COPY target/ticket-service-0.0.1-SNAPSHOT.jar /app/ticket-service.jar

# Set permissions
RUN chmod 755 /app/ticket-service.jar

# Default environment variables
ENV SPRING_PROFILES_ACTIVE=docker
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Expose port
EXPOSE 8082

# Healthcheck
HEALTHCHECK --interval=30s --timeout=10s --retries=5 --start-period=60s \
    CMD curl -f http://localhost:8082/api/tickets/health || exit 1

# Run the application
CMD ["java", "-jar", "/app/ticket-service.jar"]