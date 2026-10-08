# syntax=docker/dockerfile:1

FROM eclipse-temurin:27-jre-alpine-3.24
ARG APP_VERSION

LABEL org.opencontainers.image.authors="mirogaudi" \
      org.opencontainers.image.url="https://github.com/mirogaudi/memo-service" \
      org.opencontainers.image.version="${APP_VERSION}"

RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser

VOLUME /tmp
WORKDIR /app
COPY build/libs/memo-service-$APP_VERSION.jar app.jar
RUN mkdir -p /app/db /app/logs && chown -R appuser:appgroup /app/db /app/logs

USER appuser
EXPOSE 8080 9000

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
