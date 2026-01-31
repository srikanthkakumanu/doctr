# Stage 1: Build the application
FROM gradle:8.7-jdk21-alpine AS build
WORKDIR /home/gradle/src
COPY --chown=gradle:gradle . .
RUN gradle build --no-daemon

# Stage 2: Create the final, optimized image
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

# Expose the port the application runs on
EXPOSE 9091

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
