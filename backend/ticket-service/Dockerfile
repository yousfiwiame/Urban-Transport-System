# ============================================
# Stage 1: Build
# ============================================
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copier le pom.xml parent et celui du service pour résoudre les dépendances
COPY pom.xml ./pom.xml
COPY ticket-service/pom.xml ./ticket-service/pom.xml

# Télécharger les dépendances
RUN mvn dependency:go-offline -B -f pom.xml || true
RUN mvn dependency:go-offline -B -f ticket-service/pom.xml

# Copier le code source
COPY ticket-service/src ./ticket-service/src

# Compiler le service et créer le JAR
RUN mvn clean package -DskipTests -B -f ticket-service/pom.xml

# Vérifier que le JAR est créé
RUN test -f /app/ticket-service/target/ticket-service-*.jar || (echo "JAR file not found!" && exit 1)

# ============================================
# Stage 2: Runtime
# ============================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Installer curl et bash pour healthchecks et debug
RUN apk add --no-cache \
    curl \
    bash \
    tzdata \
    && rm -rf /var/cache/apk/*

# Configurer timezone (optionnel)
ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Créer un utilisateur non-root
RUN addgroup -S spring && adduser -S spring -G spring

# Copier le JAR depuis l'étape build
COPY --from=build --chown=spring:spring /app/ticket-service/target/ticket-service-*.jar app.jar

# Utiliser l'utilisateur non-root
USER spring:spring

# Exposer le port
EXPOSE 8082

# Healthcheck
HEALTHCHECK --interval=30s \
            --timeout=5s \
            --start-period=60s \
            --retries=3 \
            CMD curl -f http://localhost:8082/actuator/health || exit 1

# JVM optimisations
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:+UseG1GC \
               -XX:+UseStringDeduplication \
               -XX:+AlwaysPreTouch \
               -Djava.security.egd=file:/dev/./urandom"

ENV SPRING_OUTPUT_ANSI_ENABLED=always

# Commande de lancement
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]