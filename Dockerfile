# 1. JDK 17을 사용하여 빌드 (Gradle 또는 Maven)
FROM openjdk:17 AS build
WORKDIR /app
COPY . .
RUN ./gradlew build -x test || ./mvnw package -DskipTests

# 2. 실행 환경을 설정 (JDK 17)
FROM openjdk:17
WORKDIR /app

# 3. 빌드한 JAR 파일을 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 4. 애플리케이션 실행
CMD ["java", "-jar", "app.jar"]
