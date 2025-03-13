# Use an official OpenJDK runtime as a parent image
FROM openjdk:17

# Set the working directory
WORKDIR /app

# Copy the application JAR file
COPY build/libs/*.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "app.jar"]