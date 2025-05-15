# Inventory Service

This service is responsible for managing room inventory and availability in the booking system.

## Features

### Room Availability Management
- Room status tracking (Available, Temporarily Locked, Booked, Occupied, Maintenance, Cleaning)
- Temporary room locking during booking process
- Real-time availability updates
- Check-in/check-out management
- Scheduling for room cleaning and maintenance

### APIs
- Search for available rooms based on property, room type, dates, and occupancy
- Lock rooms during booking process
- Confirm/cancel bookings
- Process check-ins and check-outs

### Integration
- Kafka integration for event-driven communication with other services
- Redis for distributed locking and caching

## Development

### Prerequisites
- Java 17
- Maven
- Docker and Docker Compose
- PostgreSQL
- Redis
- Kafka

### Building the Service
```bash
cd inventory_service
mvn clean package
```

### Running Locally
```bash
mvn spring-boot:run
```

### API Documentation
When the service is running, Swagger UI is available at:
http://localhost:8083/swagger-ui.html

## Docker
The service is containerized and can be run as part of the full application stack using Docker Compose:

```bash
docker-compose up -d inventory_service
```
