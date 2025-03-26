FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Sentry Token을 build arg로 받음
ARG SENTRY_AUTH_TOKEN
ENV SENTRY_AUTH_TOKEN=$SENTRY_AUTH_TOKEN

COPY . .
# Sentry 관련 Task 제외하고 빌드
RUN ./gradlew clean build -x test -x sentryBundleSourcesJava -x sentryUploadSourceBundleJava

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar", "--spring.profiles.active=local"]
