FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

COPY . .

# 빌드 시 환경 변수를 사용하지 않도록 변경
RUN --mount=type=secret,id=sentry_token ./gradlew clean build -x test

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

CMD ["java", "-jar", "app.jar"]

EXPOSE 8080
