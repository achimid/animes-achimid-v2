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

# --- Stage 2: Glowroot Download ---
FROM alpine AS glowroot

RUN apk add --no-cache curl unzip
RUN curl -L https://github.com/glowroot/glowroot/releases/download/v0.14.7/glowroot-0.14.7-dist.zip \
        -o /tmp/glowroot.zip \
    && unzip /tmp/glowroot.zip -d /opt \
    && rm /tmp/glowroot.zip

# --- Stage 3: Runtime Stage ---
FROM amazoncorretto:25-alpine AS runner

RUN addgroup -S -g 1000 spring && adduser -S -u 1000 spring -G spring
USER spring:spring

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar
COPY --from=glowroot --chown=spring:spring /opt/glowroot /app/glowroot

RUN mkdir -p /app/glowroot/logs /app/glowroot-data \
    && printf '{\n  "users": [{"username": "anonymous", "roles": ["Administrator"]}],\n  "roles": [{"name": "Administrator", "permissions": ["agent:transaction","agent:error","agent:jvm","agent:incident","agent:config","admin"]}],\n  "web": {"port": 4000, "bindAddress": "0.0.0.0", "contextPath": "/", "sessionTimeoutMinutes": 30, "sessionCookieName": "GLOWROOT_SESSION_ID"},\n  "storage": {"rollupExpirationHours": [72,336,2160,2160], "traceExpirationHours": 336, "fullQueryTextExpirationHours": 336, "rollupCappedDatabaseSizesMb": [500,500,500,500], "traceCappedDatabaseSizeMb": 500}\n}' > /app/glowroot/admin.json

EXPOSE 3000 4000

ENTRYPOINT ["java", "-javaagent:/app/glowroot/glowroot.jar", "-Dglowroot.data.dir=/app/glowroot-data", "-Xmx512m", "-jar", "app.jar"]
