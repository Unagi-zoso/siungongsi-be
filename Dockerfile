FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

ARG SENTRY_AUTH_TOKEN
ENV SENTRY_AUTH_TOKEN=$SENTRY_AUTH_TOKEN

COPY . .
RUN ./gradlew clean build -x test -x sentryBundleSourcesJava -x sentryUploadSourceBundleJava

FROM eclipse-temurin:21-jre
WORKDIR /app

# 애플리케이션 JAR 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# entrypoint.sh 복사 및 실행 권한 부여
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

EXPOSE 8080

# 여기서 entrypoint로 전환!
ENTRYPOINT ["/entrypoint.sh"]
