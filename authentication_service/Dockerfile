
# Base image with Java 21
FROM eclipse-temurin:21-jdk-jammy AS build

# Set the working directory
WORKDIR /app

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

WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Set environment variables
ENV SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/authentication_service
ENV SPRING_DATASOURCE_USERNAME=root
ENV SPRING_DATASOURCE_PASSWORD=root
ENV SPRING_JPA_HIBERNATE_DIALECT=org.hibernate.dialect.MySQLDialect
ENV SPRING_JPA_HIBERNATE_DDL_AUTO=update
ENV SPRING_JPA_SHOW-SQL=true
ENV SPRING_DATA_REDIS_HOST=redis
ENV SPRING_DATA_REDIS_PORT=6379
ENV SPRING_KAFKA_BOOTSTRAP-SERVERS=kafka:9092
ENV APP_KAFKA_DEFAULT-SERVER=kafka:9092
ENV APP_SERVICES_NOTIFICATION_SERVICE_URL=notification_service:8080/notification_service/

# Expose the port that the app runs on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]