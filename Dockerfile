FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# SENTRY_AUTH_TOKEN을 빌드 과정에서 사용하도록 설정
ARG SENTRY_AUTH_TOKEN
ENV SENTRY_AUTH_TOKEN=$SENTRY_AUTH_TOKEN

COPY . .
RUN ./gradlew clean build -x test

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

CMD ["java", "-jar", "app.jar"]

EXPOSE 8080
