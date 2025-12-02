# ==========================
# Stage 1: Build
# ==========================
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copier le pom.xml du service
COPY pom.xml ./pom.xml

# Télécharger les dépendances
RUN mvn dependency:go-offline -B || true

# Copier le code source
COPY src ./src

# Compiler le service
RUN mvn clean package -DskipTests -B

# Vérifier que le JAR a été créé
RUN test -f /app/target/*.jar || (echo "JAR file not found!" && exit 1)

# ==========================
# Stage 2: Runtime
# ==========================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Installer curl et bash pour healthcheck/debug
RUN apk add --no-cache curl bash tzdata && rm -rf /var/cache/apk/*

# Configurer timezone
ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Créer un utilisateur non-root
RUN addgroup -S spring && adduser -S spring -G spring

# Copier le JAR depuis le build stage
COPY --from=build --chown=spring:spring /app/target/*.jar app.jar

# Utiliser l'utilisateur non-root
USER spring:spring

# Exposer le port du service
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# JVM optimisations
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:+UseG1GC \
               -XX:+UseStringDeduplication \
               -XX:+AlwaysPreTouch \
               -Djava.security.egd=file:/dev/./urandom"

# Spring Boot
ENV SPRING_OUTPUT_ANSI_ENABLED=always
ENV SPRING_PROFILES_ACTIVE=docker

# Commande de démarrage
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
