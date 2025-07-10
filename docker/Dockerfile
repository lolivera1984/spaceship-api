# syntax=docker/dockerfile:1
# Stage 1: Build the JAR file
# Use Amazon Corretto 21 Alpine image for building the project
FROM amazoncorretto:21-alpine AS build

# Install Maven using Alpine's package manager (apk)
RUN apk update && apk add maven

# Set the working directory inside the container
WORKDIR /app

# Clean Maven repository (optional but can help to avoid using corrupted dependencies)
RUN rm -rf /root/.m2/repository/*

# Copy the pom.xml file and resolve dependencies
# This will allow Maven to download all the required dependencies
COPY pom.xml .

# Run Maven with an exclusion to avoid javafx dependencies
RUN mvn dependency:go-offline -Dmaven.repo.local=/app/.m2 --no-transfer-progress

# Copy the source code into the container
COPY src /app/src

# Build the JAR file (skip tests for faster builds)
RUN mvn clean package -DskipTests

# Verify the .jar file has been generated ok
RUN ls /app/target

# Stage 2: Create a lighter image to run the application
# Use the Amazon Corretto 21 Alpine image for runtime
FROM amazoncorretto:21-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file generated from the build stage
# This ensures the JAR is available in the runtime container
COPY --from=build /app/target/spaceship-api-0.0.1-SNAPSHOT.jar /spaceship-api.jar

# Command to run the application
# The application will be started using the "java -jar" command
ENTRYPOINT ["java", "-jar", "/spaceship-api.jar"]

# Expose the port that the application will use (8080 in this case)
EXPOSE 8080
