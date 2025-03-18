FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

COPY . .
RUN ./gradlew clean build -x test -x sentryBundleSourcesJava -x sentryUploadSourceBundleJava

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

# Sentry 환경 변수 추가
ENV SENTRY_AUTH_TOKEN=${SENTRY_AUTH_TOKEN}

CMD ["java", "-jar", "app.jar", "--spring.profiles.active=dev"]

EXPOSE 8080
