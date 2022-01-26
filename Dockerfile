# syntax=docker/dockerfile:1.3

FROM bellsoft/liberica-openjdk-alpine:17
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
LABEL org.opencontainers.image.authors="mirogaudi@ya.ru" \
    org.opencontainers.image.url="https://github.com/mirogaudi/memo-service" \
    org.opencontainers.image.licenses="Apache-2.0"