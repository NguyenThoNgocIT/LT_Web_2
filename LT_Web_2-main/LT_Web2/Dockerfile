# Multi-stage Dockerfile for Spring Boot backend
# ==== Build Stage ====
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
# Copy pom first for dependency caching
COPY pom.xml .
# Download dependencies (go offline later)
RUN mvn -B -q dependency:go-offline
# Copy source
COPY src ./src
# Build (skip tests by default - can override with --build-arg SKIP_TESTS=false)
ARG SKIP_TESTS=true
RUN if [ "$SKIP_TESTS" = "true" ]; then mvn -B -q package -DskipTests; else mvn -B package; fi

# ==== Runtime Stage ====
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app
# Add non-root user
RUN addgroup -S app && adduser -S app -G app
# Copy jar from build stage
COPY --from=build /app/target/LT_Web2-0.0.1-SNAPSHOT.jar app.jar
# Expose port (matches application.properties server.port=8088)
EXPOSE 8088
# Environment variables (override in compose / Jenkins)
ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-Xms256m -Xmx512m" \
    TZ=Asia/Ho_Chi_Minh
# Healthcheck (optional simple TCP)
HEALTHCHECK --interval=30s --timeout=5s --start-period=20s --retries=3 CMD wget -qO- http://localhost:8088/actuator/health || exit 1
USER app
ENTRYPOINT ["/bin/sh","-c","java $JAVA_OPTS -jar app.jar"]
