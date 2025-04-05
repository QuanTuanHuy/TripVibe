# Notification Service with Kafka

This document outlines the architecture and implementation of the notification service which uses Kafka as a message queue for reliable email delivery.

## System Architecture

The notification system follows a microservice architecture with the following components:

```
Service 1..N → Notification Servers → Kafka → Workers → Email Service → Users
```

### Key Components

1. **Notification API Servers**
   - Provide RESTful APIs for services to send notifications
   - Validate notification requests
   - Store notifications in the database
   - Queue notifications in Kafka

2. **Kafka Message Queue**
   - Decouples notification creation from delivery
   - Buffers notifications during high load periods
   - Enables horizontal scaling of workers

3. **Email Workers**
   - Process email notifications from Kafka
   - Send emails via SMTP
   - Handle failures and retries

4. **Database**
   - Stores notification data
   - Tracks notification status and delivery attempts

5. **Retry Mechanism**
   - Automatically retries failed notifications
   - Implements exponential backoff
   - Marks notifications as failed after max retries

## Data Flow

1. A service calls the notification API to send an email
2. The notification server validates and stores the notification
3. The notification is published to the Kafka email topic
4. Email workers consume notifications from Kafka
5. Workers send emails via SMTP
6. Workers update notification status to SENT or retry on failure
7. Failed notification processor periodically checks for failed notifications and requeues them

## Configuration

### Kafka Configuration

```yaml
kafka:
  brokers:
    - localhost:9092  # Can be multiple Kafka brokers for high availability
  consumer-group-id: notification-service
  max-retry-count: 3
  email-producer-enabled: true
  email-consumer-enabled: true
```

### Email Configuration

```yaml
email:
  smtp-host: smtp.gmail.com
  smtp-port: 587
  smtp-username: your-email@gmail.com
  smtp-password: your-app-password
  from-email: notifications@example.com
```

## API Endpoints

### Send a Notification

```
POST /notification_service/api/notifications
```

Request Body:
```json
{
  "type": "EMAIL",
  "title": "Payment Confirmation",
  "content": "<p>Your payment has been confirmed.</p>",
  "userId": 123,
  "recipient": "user@example.com",
  "metadata": {
    "orderId": "ORD-12345",
    "amount": 99.99
  }
}
```

Response:
```json
{
  "id": 456,
  "type": "EMAIL",
  "title": "Payment Confirmation",
  "content": "<p>Your payment has been confirmed.</p>",
  "userId": 123,
  "recipient": "user@example.com",
  "metadata": {
    "orderId": "ORD-12345",
    "amount": 99.99
  },
  "status": "PROCESSING",
  "createdAt": 1712319780,
  "updatedAt": 1712319780
}
```

## System Reliability Features

1. **No Single Point of Failure**
   - Multiple notification servers can be deployed
   - Multiple Kafka brokers ensure message queue availability
   - Multiple workers process notifications in parallel

2. **Horizontal Scaling**
   - All components can be scaled independently
   - Notification servers, Kafka, and workers can be scaled based on load

3. **Data Persistence**
   - All notifications are stored in the database before being queued
   - Failed notifications are retried automatically

4. **Retry Mechanism**
   - Failed notifications are automatically retried with exponential backoff
   - Maximum retry count prevents infinite retry loops

## Development and Testing

### Local Development Setup

1. Start a local Kafka server:
```bash
docker-compose up -d zookeeper kafka
```

2. Run the notification service:
```bash
go run main.go
```

### Testing

Use the following curl command to test sending an email notification:

```bash
curl -X POST \
  http://localhost:8082/notification_service/api/notifications \
  -H 'Content-Type: application/json' \
  -d '{
    "type": "EMAIL",
    "title": "Test Email",
    "content": "<p>This is a test email from the notification service.</p>",
    "userId": 1,
    "recipient": "test@example.com",
    "metadata": {
      "testId": "TEST-001"
    }
  }'
```

## Monitoring

The notification system logs all significant events:
- Notification creation
- Notification queuing to Kafka
- Email sending attempts
- Success and failure events
- Retry attempts

These logs can be aggregated and monitored using standard logging tools.

## Future Enhancements

1. **Additional Notification Channels**
   - SMS notifications
   - Push notifications
   - In-app notifications

2. **Template System**
   - Reusable notification templates
   - Localization support

3. **Rate Limiting**
   - Prevent notification flooding
   - User-specific notification preferences

4. **Analytics**
   - Notification delivery success rates
   - Engagement tracking