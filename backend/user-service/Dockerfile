# ============================================
# Stage 1: Build
# ============================================
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Créer un settings.xml qui FORCE Maven Central et BLOQUE tout le reste
RUN mkdir -p /root/.m2 && \
    cat > /root/.m2/settings.xml <<'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
          http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <mirrors>
    <mirror>
      <id>central-only</id>
      <mirrorOf>*</mirrorOf>
      <name>Maven Central Only</name>
      <url>https://repo.maven.apache.org/maven2</url>
    </mirror>
  </mirrors>
</settings>
EOF

# Copier le pom.xml parent et celui du service
COPY pom.xml ./pom.xml
COPY user-service/pom.xml ./user-service/pom.xml

# Télécharger les dépendances avec Maven Central forcé
RUN mvn dependency:go-offline -B -f pom.xml || true
RUN mvn dependency:go-offline -B -f user-service/pom.xml || true

# Copier le code source
COPY user-service/src ./user-service/src

# Compiler le service et créer le JAR
RUN mvn clean package -DskipTests -B -f user-service/pom.xml

# Vérification flexible du JAR
RUN ls -la /app/user-service/target/ && \
    (test -f /app/user-service/target/user-service*.jar || \
     test -f /app/user-service/target/user-service-*.jar || \
     (echo "JAR file not found!" && exit 1))

# ============================================
# Stage 2: Runtime
# ============================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

RUN apk add --no-cache curl bash tzdata && rm -rf /var/cache/apk/*

ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

RUN addgroup -S spring && adduser -S spring -G spring

# Copier avec pattern flexible
COPY --from=build --chown=spring:spring /app/user-service/target/user-service*.jar app.jar

USER spring:spring

EXPOSE 8081

HEALTHCHECK --interval=30s \
            --timeout=5s \
            --start-period=60s \
            --retries=3 \
            CMD curl -f http://localhost:8081/actuator/health || exit 1

ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:+UseG1GC \
               -XX:+UseStringDeduplication \
               -XX:+AlwaysPreTouch \
               -Djava.security.egd=file:/dev/./urandom"

ENV SPRING_OUTPUT_ANSI_ENABLED=always

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]