# Build stage
FROM eclipse-temurin:21-jdk-noble AS builder

WORKDIR /app

COPY . .

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# Compile standalone HTTP healthcheck
RUN mkdir -p /app/healthcheck && javac -d /app/healthcheck docker-healthcheck/HealthCheck.java

# Runtime stage
FROM eclipse-temurin:21-jre-noble

WORKDIR /app

RUN useradd --system --create-home appuser

COPY --from=builder /app/target/*.jar app.jar
COPY --from=builder /app/healthcheck/HealthCheck.class /app/healthcheck/HealthCheck.class

RUN chown -R appuser:appuser /app

USER appuser

EXPOSE 8082

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD ["java", "-cp", "/app/healthcheck", "HealthCheck", "http://127.0.0.1:8082/actuator/health"]

ENTRYPOINT ["java", "-jar", "app.jar"]