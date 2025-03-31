FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

ARG SENTRY_AUTH_TOKEN
ENV SENTRY_AUTH_TOKEN=$SENTRY_AUTH_TOKEN

COPY . .
RUN ./gradlew clean build -x test -x sentryBundleSourcesJava -x sentryUploadSourceBundleJava

FROM eclipse-temurin:21-jre
WORKDIR /app

# awscli는 pip로, jq는 apt로 설치
RUN apt-get update && \
    apt-get install -y python3-pip jq && \
    pip install awscli && \
    apt-get clean

COPY --from=builder /app/build/libs/*.jar app.jar

COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

EXPOSE 8080

ENTRYPOINT ["/entrypoint.sh"]
