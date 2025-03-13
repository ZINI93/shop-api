# 1. Java 17 이미지 사용
FROM openjdk:17
WORKDIR /app

# 2. 로컬의 build/libs/ 경로에서 JAR 파일을 컨테이너로 복사
COPY build/libs/*.jar app.jar

# 3. JAR 실행
CMD ["java", "-jar", "app.jar"]