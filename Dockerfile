# 1. Java 17 이미지 사용
FROM openjdk:17
WORKDIR /app

# 2. JAR 파일 복사 (Gradle 기준)
COPY build/libs/*.jar app.jar

# 3. 실행 명령어
CMD ["java", "-jar", "app.jar"]

