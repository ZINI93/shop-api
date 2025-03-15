# 1. 빌드 스테이지 (Gradle 사용)
FROM openjdk:17 AS build
WORKDIR /app

# Gradle Wrapper 실행 권한 부여
COPY gradlew gradlew
COPY gradle gradle
RUN chmod +x gradlew

# 의존성만 먼저 다운로드 (캐시 활용)
COPY build.gradle settings.gradle ./
RUN ./gradlew dependencies --no-daemon || true

# 전체 프로젝트 복사 후 빌드
COPY . .
RUN ./gradlew build -x test || ./mvnw package -DskipTests

# 2. 실행 환경 설정
FROM openjdk:17
WORKDIR /app

# 빌드한 JAR 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 애플리케이션 실행
CMD ["java", "-jar", "app.jar"]
