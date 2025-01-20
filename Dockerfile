# OpenJDK 17 이미지 사용
FROM openjdk:17-jdk-slim

# 애플리케이션이 위치할 디렉토리 설정
WORKDIR /app

# 빌드한 JAR 파일을 컨테이너 내로 복사
COPY target/joy-project.jar /app/joy-project.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "joy-project.jar"]
