# Async Request-Reply Pattern - Complete Implementation

## 🚀 Quick Start

### Basic Usage
```go
package main

import (
    "context"
    "time"
    
    "github.com/redis/go-redis/v9"
    "go.uber.org/zap"
    
    "github.com/quantuanhuy/lib/src/config"
    "github.com/quantuanhuy/lib/src/infrastructure/async"
    "github.com/quantuanhuy/lib/src/infrastructure/msg"
)

func main() {
    // Setup
    logger, _ := zap.NewDevelopment()
    redisClient := redis.NewClient(&redis.Options{Addr: "localhost:6379"})
    producerConfig := &config.ProducerConfig{Brokers: []string{"localhost:9092"}}
    kafkaProducer, _ := msg.NewKafkaProducer(producerConfig, logger)
    
    // Create factory
    asyncConfig := config.DefaultAsyncConfig()
    factory := async.NewAsyncFactory(logger, asyncConfig, redisClient, kafkaProducer)
    
    // Get manager
    asyncManager := factory.CreateAsyncRequestManager()
    
    // Send request
    correlationID, err := asyncManager.SendRequest(ctx, "process-order", map[string]interface{}{
        "order_id": "12345",
        "amount":   100.50,
    }, 30*time.Second)
    
    // Wait for reply
    reply, err := asyncManager.WaitForReply(ctx, correlationID, 30*time.Second)
    if err == nil {
        fmt.Printf("Order processed: %+v\n", reply.Result)
    }
}
```

### With Callback
```go
// Send request with callback
callback := func(reply *entity.AsyncReply) {
    fmt.Printf("Callback received: %+v\n", reply.Result)
}

correlationID, err := asyncManager.SendRequestWithCallback(ctx, "process-payment", 
    map[string]interface{}{"amount": 250.00}, 
    30*time.Second, 
    callback)
```

## 📦 Core Components

### 1. **AsyncFactory** - Main Entry Point
```go
// Create factory with all dependencies
factory := async.NewAsyncFactory(logger, asyncConfig, redisClient, kafkaProducer)

// Get components
asyncManager := factory.CreateAsyncRequestManager()
correlationMgr := factory.CreateCorrelationManager()
requestHandler := factory.CreateRequestHandler()
replyHandler := factory.CreateReplyHandler()
```

### 2. **AsyncRequestManager** - Request Management
```go
// Send requests
correlationID, err := asyncManager.SendRequest(ctx, requestType, payload, timeout)
correlationID, err := asyncManager.SendRequestWithCallback(ctx, requestType, payload, timeout, callback)

// Wait for replies
reply, err := asyncManager.WaitForReply(ctx, correlationID, timeout)

// Cancel requests
err := asyncManager.CancelRequest(ctx, correlationID)

// Get status
status, err := asyncManager.GetRequestStatus(ctx, correlationID)
```

### 3. **CorrelationManager** - Correlation Storage
```go
// Store correlation
err := correlationMgr.StoreCorrelation(ctx, correlationID, request, callback)

// Get correlation
request, callback, err := correlationMgr.GetCorrelation(ctx, correlationID)

// Cleanup
err := correlationMgr.CleanupExpired(ctx)
count, err := correlationMgr.GetPendingCount(ctx)
```

## ⚙️ Configuration

### Complete Configuration
```go
config := &config.AsyncConfig{
    // Topics
    RequestTopic: "async-requests",
    ReplyTopic:   "async-replies",
    
    // Timeouts
    DefaultTimeout:  30 * time.Second,
    RequestTimeout:  60 * time.Second,
    ReplyTimeout:    30 * time.Second,
    
    // Concurrency
    MaxConcurrency:     100,
    MaxRequestHandlers: 10,
    MaxReplyHandlers:   10,
    
    // Cleanup
    CleanupInterval:     5 * time.Minute,
    CorrelationTTL:      1 * time.Hour,
    ExpiredCleanupBatch: 100,
    
    // Retry
    RetryAttempts:    3,
    RetryDelay:       time.Second,
    RetryBackoffRate: 2.0,
    
    // Monitoring
    MetricsEnabled:      true,
    MonitoringInterval:  30 * time.Second,
}
```

### Default Configuration
```go
config := config.DefaultAsyncConfig()
```

## 🔧 Advanced Features

### 1. Request Handlers
```go
// Register custom handlers
asyncManager.RegisterHandler("process-order", func(request *entity.AsyncRequest) (*entity.AsyncReply, error) {
    // Process order logic
    result := map[string]interface{}{
        "order_id": request.Payload["order_id"],
        "status":   "processed",
    }
    
    return &entity.AsyncReply{
        CorrelationID: request.CorrelationID,
        Status:        entity.AsyncRequestStatusCompleted,
        Result:        result,
    }, nil
})
```

### 2. Background Processing
```go
// Start processing
ctx, cancel := context.WithCancel(context.Background())
go asyncManager.StartProcessing(ctx)

// Stop processing
cancel()
asyncManager.StopProcessing(ctx)
```

### 3. Cleanup Worker
```go
// Start cleanup worker
go factory.StartCleanupWorker(ctx)
```

### 4. Metrics
```go
metrics := factory.GetMetrics()
fmt.Printf("Metrics: %+v\n", metrics)
```

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   AsyncFactory  │───▶│ AsyncRequestMgr │───▶│CorrelationMgr  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│ RequestHandler  │    │  ReplyHandler   │    │ CallbackManager │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│ Kafka Producer  │    │  Redis Client   │    │ Memory Storage  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 📋 Status Types

```go
const (
    AsyncRequestStatusPending    = "pending"
    AsyncRequestStatusProcessing = "processing"
    AsyncRequestStatusCompleted  = "completed"
    AsyncRequestStatusFailed     = "failed"
    AsyncRequestStatusTimeout    = "timeout"
    AsyncRequestStatusCancelled  = "cancelled"
)
```

## 🔍 Message Types

```go
const (
    MessageTypeRequest   = "request"
    MessageTypeReply     = "reply"
    MessageTypeHeartbeat = "heartbeat"
    MessageTypeCancel    = "cancel"
)
```

## 📝 Entity Structures

### AsyncRequest
```go
type AsyncRequest struct {
    ID            string                 `json:"id"`
    CorrelationID string                 `json:"correlation_id"`
    RequestType   string                 `json:"request_type"`
    Status        AsyncRequestStatus     `json:"status"`
    Payload       map[string]interface{} `json:"payload"`
    ReplyTo       string                 `json:"reply_to"`
    Timeout       time.Duration          `json:"timeout"`
    CreatedAt     time.Time              `json:"created_at"`
    UpdatedAt     time.Time              `json:"updated_at"`
    ProcessedAt   *time.Time             `json:"processed_at,omitempty"`
    CompletedAt   *time.Time             `json:"completed_at,omitempty"`
    Error         string                 `json:"error,omitempty"`
}
```

### AsyncReply
```go
type AsyncReply struct {
    ID            string                 `json:"id"`
    CorrelationID string                 `json:"correlation_id"`
    RequestID     string                 `json:"request_id"`
    Status        AsyncRequestStatus     `json:"status"`
    Result        map[string]interface{} `json:"result,omitempty"`
    Error         string                 `json:"error,omitempty"`
    ProcessedAt   time.Time              `json:"processed_at"`
    Duration      time.Duration          `json:"duration"`
}
```

## 🧪 Testing

### Unit Tests
```bash
go test ./src/infrastructure/async/ -v
```

### Integration Tests
```bash
go test ./test/ -v
```

### Example
```bash
go run examples/async_implementation_demo.go
```

## 📦 Dependencies

```go
// Core dependencies
"github.com/redis/go-redis/v9"
"go.uber.org/zap"  
"github.com/google/uuid"

// Testing
"github.com/stretchr/testify"
```

## 🚀 Production Considerations

### Scaling
- Redis cluster for correlation storage
- Kafka partitioning for horizontal scaling
- Multiple service instances

### Monitoring
- Structured logging with correlation IDs
- Metrics collection
- Health checks

### Security
- Redis authentication
- Kafka SSL/SASL
- Message encryption

## 📖 Complete Example

See `examples/async_implementation_demo.go` for a complete working example with:
- Factory setup
- Request sending
- Callback handling
- Cleanup worker
- Metrics collection
- Graceful shutdown

## 🔧 Troubleshooting

### Common Issues
1. **Redis Connection**: Check Redis server and connection settings
2. **Kafka Producer**: Verify Kafka broker connectivity
3. **Timeout Issues**: Adjust timeout values in config
4. **Memory Usage**: Monitor callback cleanup

### Debug Mode
```go
logger, _ := zap.NewDevelopment()
```

## 📄 License

This implementation is part of the common library and follows the project's license terms.
