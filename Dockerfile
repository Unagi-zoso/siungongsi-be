# OpenJDK 21 기반 이미지 사용
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY target/*.jar app.jar

CMD ["java", "-jar", "app.jar"]

EXPOSE 8080
