# 1. JDK 17을 사용하여 빌드
FROM openjdk:17 AS build
WORKDIR /app

# gradlew 및 필요한 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

# gradlew 실행 권한 추가
RUN chmod +x gradlew

# 빌드 실행
RUN ./gradlew build -x test || ./mvnw package -DskipTests

# 2. 실행 환경을 설정
FROM openjdk:17
WORKDIR /app

# 3. 빌드한 JAR 파일을 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 4. 애플리케이션 실행
CMD ["java", "-jar", "app.jar"]
