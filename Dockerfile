FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Gradle 빌드 후 생성된 JAR 복사
COPY build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]