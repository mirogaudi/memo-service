# syntax=docker/dockerfile:1

FROM eclipse-temurin:21-jdk
LABEL org.opencontainers.image.authors="mirogaudi" \
    org.opencontainers.image.url="https://github.com/mirogaudi/memo-service"
VOLUME /tmp
COPY build/libs/memo-service-*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
