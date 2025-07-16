# Async Request-Reply Pattern Implementation

## Overview
This implementation provides a complete asynchronous request-reply pattern for Go applications using Redis for correlation management and Kafka for message transport.

## Architecture

### Core Components
1. **RedisCorrelationManager** - Manages correlation data using Redis
2. **KafkaAsyncRequestManager** - Handles async requests using Kafka
3. **RequestMessageHandler** - Processes incoming request messages
4. **ReplyMessageHandler** - Processes incoming reply messages
5. **AsyncFactory** - Factory for creating and managing all components

### Key Features
- ✅ **Asynchronous Processing**: Non-blocking request-reply pattern
- ✅ **Correlation Management**: Redis-based correlation storage with TTL
- ✅ **Message Transport**: Kafka-based reliable message delivery
- ✅ **Callback Support**: Optional callback functions for async replies
- ✅ **Request Cancellation**: Cancel pending requests
- ✅ **Automatic Cleanup**: Background cleanup of expired correlations
- ✅ **Metrics**: Component metrics and monitoring
- ✅ **Error Handling**: Comprehensive error handling and logging

## Implementation Files

### Core Infrastructure
- `src/infrastructure/async/redis_correlation_manager.go` - Redis-based correlation management
- `src/infrastructure/async/kafka_async_request_manager.go` - Kafka-based async request handling
- `src/infrastructure/async/request_handler.go` - Request message handler
- `src/infrastructure/async/reply_handler.go` - Reply message handler
- `src/infrastructure/async/factory.go` - Factory for component creation
- `src/infrastructure/async/callback_manager.go` - In-memory callback management
- `src/infrastructure/async/async.go` - Package documentation

### Interfaces
- `src/core/port/correlation_manager.go` - Correlation manager interface
- `src/core/port/async_request_manager.go` - Async request manager interface

### Entities
- `src/core/entity/async/async_request.go` - Request entity
- `src/core/entity/async/async_reply.go` - Reply entity
- `src/core/entity/async/async_message.go` - Message entity

## Usage

### Basic Usage
```go
// Create factory
factory := async.NewAsyncFactory(logger, asyncConfig, redisClient, kafkaProducer)

// Create async request manager
asyncManager := factory.CreateAsyncRequestManager()

// Send request
correlationID, err := asyncManager.SendRequest(ctx, "user-profile", payload, 30*time.Second)

// Wait for reply
reply, err := asyncManager.WaitForReply(ctx, correlationID, 30*time.Second)
```

### With Callback
```go
callback := func(reply *entity.AsyncReply) {
    log.Printf("Received reply: %+v", reply)
}

correlationID, err := asyncManager.SendRequestWithCallback(ctx, "order-status", payload, 30*time.Second, callback)
```

### Cancel Request
```go
err := asyncManager.CancelRequest(ctx, correlationID)
```

## Configuration

### AsyncConfig
```go
type AsyncConfig struct {
    RequestTimeout   time.Duration // Default request timeout
    CleanupInterval  time.Duration // Cleanup interval for expired correlations
    RequestTopic     string        // Kafka topic for requests
    ReplyTopic       string        // Kafka topic for replies
}
```

### Redis Configuration
```go
redisClient := redis.NewClient(&redis.Options{
    Addr:     "localhost:6379",
    Password: "",
    DB:       0,
})
```

### Kafka Configuration
```go
producerConfig := &config.ProducerConfig{
    Brokers: []string{"localhost:9092"},
}
```

## Testing

### Unit Tests
```bash
go test ./src/infrastructure/async/... -v
```

### Integration Tests
```bash
go test ./tests/... -v
```

## Examples

### Complete Example
See `examples/async_implementation_demo.go` for a complete working example that demonstrates:
- Sending requests without callbacks
- Sending requests with callbacks
- Canceling requests
- Cleanup worker
- Metrics collection

### Running Example
```bash
go run examples/async_implementation_demo.go
```

## Dependencies

### Core Dependencies
- `github.com/redis/go-redis/v9` - Redis client
- `go.uber.org/zap` - Structured logging
- `github.com/google/uuid` - UUID generation

### Testing Dependencies
- `github.com/stretchr/testify` - Testing framework

## Error Handling

The implementation provides comprehensive error handling:
- Connection errors (Redis, Kafka)
- Timeout errors
- Serialization errors
- Correlation not found errors
- Request cancellation errors

## Performance Considerations

### Redis Connection Pool
- Uses connection pooling for Redis operations
- Configurable connection pool settings

### Kafka Producer
- Asynchronous message sending
- Batch processing support
- Configurable retry policies

### Memory Management
- Automatic cleanup of expired correlations
- Configurable cleanup intervals
- Memory-efficient correlation storage

## Monitoring

### Metrics
- Active correlations count
- Request processing time
- Success/failure rates
- Component health status

### Logging
- Structured logging with correlation IDs
- Request/reply tracing
- Error logging with context

## Production Considerations

### Scaling
- Multiple instances can share Redis correlation store
- Kafka partitioning for horizontal scaling
- Load balancing across instances

### Security
- Redis authentication
- Kafka SSL/SASL configuration
- Message encryption support

### Reliability
- Redis persistence configuration
- Kafka replication and durability
- Circuit breaker patterns

## Migration Guide

### From Interface-only to Full Implementation
If you were previously using only the interfaces, you can now use the complete implementation:

1. Update imports to include infrastructure packages
2. Replace mock implementations with real ones
3. Configure Redis and Kafka connections
4. Use AsyncFactory for component creation

### Configuration Changes
- Update configuration structures to match new format
- Add Redis and Kafka connection settings
- Configure cleanup intervals and timeouts

## Troubleshooting

### Common Issues
1. **Redis Connection Failed**: Check Redis server status and connection settings
2. **Kafka Producer Error**: Verify Kafka broker connectivity
3. **Correlation Not Found**: Check TTL settings and cleanup intervals
4. **Request Timeout**: Adjust timeout values based on processing time

### Debug Mode
Enable debug logging to see detailed operation traces:
```go
logger, _ := zap.NewDevelopment()
```

## Contributing

When contributing to this implementation:
1. Follow Go best practices
2. Add comprehensive tests
3. Update documentation
4. Maintain backward compatibility
5. Include performance benchmarks

## License

This implementation is part of the common library and follows the project's license terms.
