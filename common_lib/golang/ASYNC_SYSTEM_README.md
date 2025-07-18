# Async Request-Reply System

## Overview

This is a comprehensive async request-reply system implementation following Clean Architecture principles. The system provides reliable, scalable async communication using Kafka for messaging and Redis for correlation management.

## Architecture

### Clean Architecture Layers

1. **Entities** (`src/core/entity/async/`)
   - `AsyncRequest`: Core business entity for async requests
   - `AsyncReply`: Core business entity for async replies
   - `CorrelationData`: Entity for managing correlation information
   - `AsyncMessage`: Message envelope for Kafka communication

2. **Ports** (`src/core/port/`)
   - `IAsyncRequestManager`: Interface for managing async requests
   - `IAsyncRequestHandler`: Interface for handling request processing
   - `IAsyncReplyHandler`: Interface for handling reply processing
   - `ICorrelationPort`: Interface for correlation data persistence
   - `ICallbackManager`: Interface for callback management

3. **Use Cases/Services** (`src/core/service/`)
   - `AsyncRequestManager`: Coordinates async request-reply operations
   - `AsyncRequestHandler`: Handles incoming async requests
   - `AsyncReplyHandler`: Handles incoming async replies
   - `ReplyMessageHandler`: Processes Kafka reply messages
   - `AsyncSystemBootstrap`: Factory for creating and wiring components

4. **Infrastructure** (`src/infrastructure/async/`)
   - `RedisCorrelationManager`: Redis-based correlation persistence
   - `CallbackManager`: In-memory callback management

## Key Features

### 1. Thread-Safe Operations
- All components are designed to be thread-safe
- Proper mutex usage for concurrent access
- Race condition prevention

### 2. Proper Error Handling
- Comprehensive error handling throughout the system
- Panic recovery in callback execution
- Graceful degradation on failures

### 3. Timeout Management
- Configurable timeouts for requests
- Automatic cleanup of expired correlations
- Timeout handling in callbacks

### 4. Callback Support
- Async callback execution for non-blocking operations
- Callback timeout protection
- Panic recovery in callback execution

### 5. Monitoring and Observability
- Comprehensive logging with structured fields
- Health check capabilities
- Metrics support (configurable)

## Usage Examples

### Basic Setup

```go
// Create configuration
config := service.CreateDefaultAsyncConfig()

// Create bootstrap
bootstrap := service.NewAsyncSystemBootstrap(
    logger,
    config,
    redisClient,
    kafkaProducer,
)

// Create and start system
asyncSystem := bootstrap.CreateAsyncSystem()
ctx := context.Background()
asyncSystem.Start(ctx)
defer asyncSystem.Stop(ctx)
```

### Send Request with Callback

```go
requestManager := asyncSystem.GetRequestManager()

correlationID, err := requestManager.SendRequestWithCallback(
    ctx,
    "user.create",
    map[string]interface{}{
        "name":  "John Doe",
        "email": "john@example.com",
    },
    30*time.Second,
    func(reply *entity.AsyncReply) {
        if reply.IsSuccess() {
            log.Printf("User created: %+v", reply.Result)
        } else {
            log.Printf("Error: %s", reply.Error)
        }
    },
)
```

### Send Request and Wait for Reply

```go
correlationID, err := requestManager.SendRequest(
    ctx,
    "order.process",
    map[string]interface{}{
        "order_id": "ORDER-123",
        "amount":   100.50,
    },
    30*time.Second,
)

reply, err := requestManager.WaitForReply(ctx, correlationID, 30*time.Second)
if err != nil {
    log.Printf("Error waiting for reply: %v", err)
    return
}

if reply.IsSuccess() {
    log.Printf("Order processed: %+v", reply.Result)
}
```

### Register Custom Request Handler

```go
requestHandler := asyncSystem.GetRequestHandler()

requestHandler.RegisterHandler("user.create", &UserCreateHandler{
    logger: logger,
})
```

### Custom Request Handler Implementation

```go
type UserCreateHandler struct {
    logger *zap.Logger
}

func (h *UserCreateHandler) Handle(request *entity.AsyncRequest) (*entity.AsyncReply, error) {
    // Process the request
    name, _ := request.Payload["name"].(string)
    email, _ := request.Payload["email"].(string)

    // Simulate processing
    time.Sleep(100 * time.Millisecond)

    // Create reply
    return &entity.AsyncReply{
        ID:            "reply-" + request.ID,
        CorrelationID: request.CorrelationID,
        RequestID:     request.ID,
        Status:        entity.AsyncRequestStatusCompleted,
        Result: map[string]interface{}{
            "user_id": "USER-12345",
            "name":    name,
            "email":   email,
        },
        ProcessedAt: time.Now(),
        Duration:    100 * time.Millisecond,
    }, nil
}
```

## Configuration

### Default Configuration

```go
config := &config.AsyncConfig{
    RequestTopic:        "async-requests",
    ReplyTopic:          "async-replies",
    DefaultTimeout:      30 * time.Second,
    RequestTimeout:      60 * time.Second,
    ReplyTimeout:        30 * time.Second,
    MaxConcurrency:      100,
    MaxRequestHandlers:  10,
    MaxReplyHandlers:    10,
    CleanupInterval:     5 * time.Minute,
    CorrelationTTL:      1 * time.Hour,
    ExpiredCleanupBatch: 100,
    RetryAttempts:       3,
    RetryDelay:          1 * time.Second,
    RetryBackoffRate:    2.0,
    MetricsEnabled:      true,
    MonitoringInterval:  1 * time.Minute,
}
```

## Best Practices

### 1. Resource Management
- Always start and stop the system properly
- Use context for cancellation
- Implement proper cleanup in handlers

### 2. Error Handling
- Handle all errors appropriately
- Use structured logging
- Implement proper fallback mechanisms

### 3. Performance
- Register handlers early in application lifecycle
- Use appropriate timeout values
- Monitor system performance and adjust concurrency settings

### 4. Monitoring
- Enable metrics collection
- Monitor correlation data growth
- Set up alerts for failed requests

## Troubleshooting

### Common Issues

1. **Timeout Errors**
   - Check network connectivity
   - Verify Kafka and Redis availability
   - Adjust timeout configurations

2. **Memory Issues**
   - Monitor correlation data cleanup
   - Check for callback memory leaks
   - Adjust cleanup intervals

3. **Performance Issues**
   - Monitor concurrent request load
   - Check Redis and Kafka performance
   - Adjust concurrency settings

### Health Checks

```go
if err := bootstrap.HealthCheck(ctx); err != nil {
    log.Printf("Health check failed: %v", err)
}
```

## Testing

The system includes comprehensive test coverage:

- Unit tests for all components
- Integration tests for Redis and Kafka
- Performance tests for concurrent operations
- Callback timeout tests

## Migration from Legacy Code

The system provides backward compatibility through legacy components:

```go
// Create legacy components
replyHandler, requestHandler := bootstrap.CreateLegacyComponents()
```

## Future Enhancements

1. **Distributed Tracing**: Add OpenTelemetry support
2. **Circuit Breaker**: Implement circuit breaker pattern
3. **Rate Limiting**: Add rate limiting for request processing
4. **Persistent Queues**: Support for persistent message queues
5. **Multi-tenant Support**: Add tenant isolation capabilities
