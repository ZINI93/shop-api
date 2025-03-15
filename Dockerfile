# Stage 1: Build the application
FROM openjdk:17-slim AS build
WORKDIR /app

# 필요한 패키지 설치
RUN apt-get update && apt-get install -y findutils

# Gradle 설정 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

# gradlew 실행 권한 부여
RUN chmod +x gradlew

# 의존성 다운로드
RUN ./gradlew dependencies --no-daemon

# 프로젝트 빌드
RUN ./gradlew build -x test

# Stage 2: Create the final image
FROM openjdk:17-slim
WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 애플리케이션 실행
EXPOSE 8080
CMD ["java", "-jar", "app.jar", "--server.port=${PORT}"]