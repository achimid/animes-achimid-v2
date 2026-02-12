# --- Stage 1: Build Stage ---
# We use the Corretto 25 Alpine image as the base for building
FROM amazoncorretto:24-alpine AS builder

# Install necessary build tools for Alpine (Gradlew needs bash and lib64-compat for some plugins)
RUN apk add --no-cache bash libc6-compat

WORKDIR /app

# Copy the Gradle wrapper and configuration files
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Ensure the wrapper is executable
RUN chmod +x gradlew

# Download dependencies (this layer is cached)
RUN ./gradlew dependencies --no-daemon

# Copy the source code and build the application
COPY src src
RUN ./gradlew bootJar --no-daemon

# --- Stage 2: Runtime Stage ---
FROM amazoncorretto:24-alpine AS runner

# Security: Create a non-root user to run the application
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /app

# Copy the built JAR from the builder stage
# Spring Boot typically names the jar based on project name and version
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the standard Spring Boot port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-Xmx512m",  "-jar", "app.jar"]