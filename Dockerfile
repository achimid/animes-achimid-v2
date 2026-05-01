# --- Stage 1: Build Stage ---
FROM amazoncorretto:25-alpine AS builder

RUN apk add --no-cache bash libc6-compat

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon

COPY src src
RUN ./gradlew bootJar -x test --no-daemon

# --- Stage 2: Runtime Stage ---
FROM amazoncorretto:25-alpine AS runner

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Xmx512m", "-jar", "app.jar"]
