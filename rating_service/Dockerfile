
# Base image with Java 21
FROM eclipse-temurin:21-jdk-jammy AS build

# Set the working directory
WORKDIR /backend/rating_service/

# Copy Maven wrapper and pom.xml
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Download dependencies (this layer can be cached if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline

# Copy the source code
COPY src/ ./src/

# Build the application
RUN ./mvnw clean package -DskipTests

# Create the runtime image
FROM eclipse-temurin:21-jre-jammy

WORKDIR /backend/rating_service/

# Copy the built JAR from the build stage
COPY --from=build /backend/rating_service/target/*.jar rating_service.jar

COPY src/main/resources/application-docker.yml application.yml

EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "rating_service.jar", "--spring.config.location=file:/backend/rating_service/application.yml"]