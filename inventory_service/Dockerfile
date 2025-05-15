FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/inventory-service-0.0.1-SNAPSHOT.jar inventory-service.jar

EXPOSE 8083

# Database, Redis, and Kafka environment variables
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/inventory_db
ENV SPRING_DATASOURCE_USERNAME=postgres
ENV SPRING_DATASOURCE_PASSWORD=postgres
ENV SPRING_DATA_REDIS_HOST=redis
ENV SPRING_DATA_REDIS_PORT=6379
ENV SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092

ENTRYPOINT ["java", "-jar", "inventory-service.jar"]
