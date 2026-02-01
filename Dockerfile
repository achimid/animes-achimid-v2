# Stage 1: Build with Gradle
FROM eclipse-temurin:25-jdk-jammy AS build
WORKDIR /app
COPY . .

# Stage 2: Run with JRE (minimal image)
FROM eclipse-temurin:25-jre-jammy
WORKDIR /app
# Copy the built jar from the build stage
COPY --from=build /app/build/libs/*.jar app.jar
# Expose port (default 8080)
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]