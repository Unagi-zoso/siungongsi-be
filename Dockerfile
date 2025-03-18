FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

COPY . .

# 🔹 빌드 시 Sentry 관련 태스크 제외
RUN ./gradlew clean build -x test -x sentryBundleSourcesJava -x sentryUploadSourceBundleJava

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

CMD ["java", "-jar", "app.jar"]

EXPOSE 8080
