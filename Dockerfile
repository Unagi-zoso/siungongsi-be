FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

COPY . .
RUN ./gradlew clean build -x test -x sentryBundleSourcesJava -x sentryUploadSourceBundleJava

FROM eclipse-temurin:21-jre
WORKDIR /app

# 타임존 설정
RUN apt-get update && apt-get install -y tzdata && \
    ln -sf /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
    echo "Asia/Seoul" > /etc/timezone && \
    dpkg-reconfigure -f noninteractive tzdata && \
    apt-get clean

COPY --from=builder /app/build/libs/*.jar app.jar

# Sentry 환경 변수
ENV SENTRY_AUTH_TOKEN=${SENTRY_AUTH_TOKEN}

CMD ["java", "-jar", "app.jar", "--spring.profiles.active=dev"]

EXPOSE 8080
