# 1. JDK 17을 사용하여 빌드
FROM openjdk:17 AS build
WORKDIR /app
COPY . .
RUN ./gradlew build || ./mvnw package

# 2. 실행 환경 설정
FROM openjdk:17
WORKDIR /app

# 3. 빌드한 JAR 파일을 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 4. 실행
CMD ["java", "-jar", "app.jar"]
