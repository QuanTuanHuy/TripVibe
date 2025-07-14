# Kafka Library Documentation

## Overview

This Kafka library provides a comprehensive, enterprise-grade implementation for Apache Kafka operations in Go applications. The library is built using clean architecture principles and provides three main components:

- **Producer**: High-performance message publishing with support for synchronous/asynchronous operations, batching, transactions, and compression
- **Consumer**: Reliable message consumption with consumer group management, offset control, and automatic rebalancing  
- **Admin**: Complete cluster administration including topic management, consumer group operations, and metadata retrieval

## Features

### Producer Features
- ✅ **Synchronous and Asynchronous Messaging**: Send messages with immediate confirmation or fire-and-forget
- ✅ **Batch Operations**: Efficient bulk message sending for high throughput
- ✅ **Compression Support**: GZIP, Snappy, LZ4, and ZSTD compression algorithms
- ✅ **Transactional Support**: ACID transactions for exactly-once semantics
- ✅ **Partition Control**: Send messages to specific partitions
- ✅ **Idempotent Producer**: Prevent duplicate messages
- ✅ **Configurable Retries**: Robust error handling with exponential backoff
- ✅ **Security**: SASL/SSL authentication support

### Consumer Features
- ✅ **Consumer Groups**: Coordinated consumption with automatic partition assignment
- ✅ **Multiple Rebalance Strategies**: Range, Round-Robin, and Sticky assignment
- ✅ **Offset Management**: Manual and automatic offset commits
- ✅ **Pattern Subscriptions**: Subscribe to topics matching patterns
- ✅ **Seeking**: Seek to specific offsets, beginning, or end of partitions
- ✅ **Pause/Resume**: Dynamic flow control
- ✅ **Configurable Fetching**: Optimize for latency or throughput
- ✅ **Security**: SASL/SSL authentication support

### Admin Features
- ✅ **Topic Management**: Create, delete, describe, and list topics
- ✅ **Partition Management**: Add partitions to existing topics
- ✅ **Topic Configuration**: Modify topic-level settings
- ✅ **Consumer Group Management**: List, describe, and delete consumer groups
- ✅ **Offset Management**: List and reset consumer group offsets
- ✅ **Cluster Metadata**: Retrieve broker and topic information
- ✅ **Security**: SASL/SSL authentication support

## Architecture

The library follows clean architecture principles:

```
├── core/port/              # Interface definitions and data structures
│   └── kafka_port.go       # Kafka interfaces and configuration types
└── infrastructure/service/ # Concrete implementations
    ├── kafka_producer.go   # Producer implementation using Sarama
    ├── kafka_consumer.go   # Consumer implementation using Sarama
    └── kafka_admin.go      # Admin implementation using Sarama
```

## Installation

```bash
go get github.com/IBM/sarama
go get go.uber.org/zap
```

## Quick Start

### Producer Example

```go
package main

import (
    "context"
    "log"
    "time"

    "github.com/quantuanhuy/lib/src/core/port"
    "github.com/quantuanhuy/lib/src/infrastructure/service"
    "go.uber.org/zap"
)

func main() {
    logger, _ := zap.NewProduction()
    defer logger.Sync()

    // Configure producer
    config := &port.ProducerConfig{
        Brokers:         []string{"localhost:9092"},
        RequiredAcks:    1,
        RetryMax:        3,
        CompressionType: "gzip",
    }

    // Create producer
    producer, err := service.NewKafkaProducer(config, logger)
    if err != nil {
        log.Fatal(err)
    }
    defer producer.Close()

    // Send message
    message := &port.KafkaMessage{
        Topic: "my-topic",
        Key:   []byte("my-key"),
        Value: []byte("Hello, Kafka!"),
        Headers: map[string][]byte{
            "source": []byte("my-service"),
        },
    }

    ctx := context.Background()
    err = producer.SendMessage(ctx, message)
    if err != nil {
        log.Fatal(err)
    }

    log.Printf("Message sent to partition %d, offset %d", message.Partition, message.Offset)
}
```

### Consumer Example

```go
package main

import (
    "context"
    "log"
    "time"

    "github.com/quantuanhuy/lib/src/core/port"
    "github.com/quantuanhuy/lib/src/infrastructure/service"
    "go.uber.org/zap"
)

func main() {
    logger, _ := zap.NewProduction()
    defer logger.Sync()

    // Configure consumer
    config := &port.ConsumerConfig{
        Brokers:         []string{"localhost:9092"},
        GroupID:         "my-consumer-group",
        AutoOffsetReset: "earliest",
        EnableAutoCommit: true,
    }

    // Create consumer
    consumer, err := service.NewKafkaConsumer(config, logger)
    if err != nil {
        log.Fatal(err)
    }
    defer consumer.Close()

    // Message handler
    handler := func(message *port.KafkaMessage) error {
        log.Printf("Received: %s from partition %d, offset %d", 
            string(message.Value), message.Partition, message.Offset)
        return nil
    }

    // Start consuming
    ctx := context.Background()
    err = consumer.Subscribe(ctx, []string{"my-topic"}, handler)
    if err != nil {
        log.Fatal(err)
    }
}
```

### Admin Example

```go
package main

import (
    "context"
    "log"

    "github.com/quantuanhuy/lib/src/core/port"
    "github.com/quantuanhuy/lib/src/infrastructure/service"
    "go.uber.org/zap"
)

func main() {
    logger, _ := zap.NewProduction()
    defer logger.Sync()

    // Configure admin
    config := &port.AdminConfig{
        Brokers: []string{"localhost:9092"},
        RequestTimeout: 30 * time.Second,
    }

    // Create admin client
    admin, err := service.NewKafkaAdmin(config, logger)
    if err != nil {
        log.Fatal(err)
    }
    defer admin.Close()

    ctx := context.Background()

    // Create topic
    topicConfig := &port.TopicConfig{
        Name:              "my-topic",
        NumPartitions:     3,
        ReplicationFactor: 1,
    }

    err = admin.CreateTopic(ctx, topicConfig)
    if err != nil {
        log.Fatal(err)
    }

    // List topics
    topics, err := admin.ListTopics(ctx)
    if err != nil {
        log.Fatal(err)
    }

    log.Printf("Topics: %v", topics)
}
```

## Configuration

### Producer Configuration

```go
type ProducerConfig struct {
    // Connection settings
    Brokers []string
    
    // Acknowledgment settings
    RequiredAcks int16 // 0=no ack, 1=leader ack, -1=all replicas ack
    
    // Retry settings
    RetryMax     int
    RetryBackoff time.Duration
    
    // Message settings
    MaxMessageBytes int
    
    // Compression settings
    CompressionType string // "none", "gzip", "snappy", "lz4", "zstd"
    
    // Batching settings
    FlushFrequency time.Duration
    FlushMessages  int
    FlushBytes     int
    
    // Transaction settings
    TransactionID string
    
    // Security settings
    SecurityProtocol string // "PLAINTEXT", "SASL_PLAINTEXT", "SASL_SSL", "SSL"
    SASLMechanism    string // "PLAIN", "SCRAM-SHA-256", "SCRAM-SHA-512"
    SASLUsername     string
    SASLPassword     string
}
```

### Consumer Configuration

```go
type ConsumerConfig struct {
    // Connection settings
    Brokers []string
    GroupID string
    
    // Offset settings
    AutoOffsetReset     string // "earliest", "latest"
    EnableAutoCommit    bool
    AutoCommitInterval  time.Duration
    
    // Session settings
    SessionTimeout    time.Duration
    HeartbeatInterval time.Duration
    
    // Rebalance settings
    RebalanceStrategy string // "range", "roundrobin", "sticky"
    
    // Fetch settings
    FetchMinBytes int32
    FetchMaxBytes int32
    FetchMaxWait  time.Duration
    
    // Security settings
    SecurityProtocol string
    SASLMechanism    string
    SASLUsername     string
    SASLPassword     string
}
```

### Admin Configuration

```go
type AdminConfig struct {
    // Connection settings
    Brokers []string
    
    // Request settings
    RequestTimeout time.Duration
    
    // Retry settings
    RetryMax     int
    RetryBackoff time.Duration
    
    // Security settings
    SecurityProtocol string
    SASLMechanism    string
    SASLUsername     string
    SASLPassword     string
}
```

## Advanced Usage

### Transactional Producer

```go
config := &port.ProducerConfig{
    Brokers:           []string{"localhost:9092"},
    RequiredAcks:      -1,
    EnableIdempotence: true,
    TransactionID:     "my-transaction-id",
}

producer, err := service.NewKafkaProducer(config, logger)
if err != nil {
    log.Fatal(err)
}
defer producer.Close()

ctx := context.Background()

// Begin transaction
err = producer.BeginTransaction(ctx)
if err != nil {
    log.Fatal(err)
}

// Send messages
for _, message := range messages {
    err = producer.SendMessage(ctx, message)
    if err != nil {
        producer.AbortTransaction(ctx)
        log.Fatal(err)
    }
}

// Commit transaction
err = producer.CommitTransaction(ctx)
if err != nil {
    log.Fatal(err)
}
```

### Batch Processing

```go
// Create multiple messages
messages := make([]*port.KafkaMessage, 100)
for i := 0; i < 100; i++ {
    messages[i] = &port.KafkaMessage{
        Topic: "batch-topic",
        Key:   []byte(fmt.Sprintf("key-%d", i)),
        Value: []byte(fmt.Sprintf("message-%d", i)),
    }
}

// Send as batch for better performance
err = producer.SendMessages(ctx, messages)
if err != nil {
    log.Fatal(err)
}
```

### Manual Offset Management

```go
config := &port.ConsumerConfig{
    Brokers:          []string{"localhost:9092"},
    GroupID:          "manual-commit-group",
    EnableAutoCommit: false, // Disable auto commit
}

consumer, err := service.NewKafkaConsumer(config, logger)
if err != nil {
    log.Fatal(err)
}

handler := func(message *port.KafkaMessage) error {
    // Process message
    processMessage(message)
    
    // Manually commit offset
    offsets := map[string]map[int32]int64{
        message.Topic: {
            message.Partition: message.Offset + 1,
        },
    }
    
    return consumer.CommitOffsets(ctx, offsets)
}
```

### Security Configuration

```go
// SASL/SSL configuration
config := &port.ProducerConfig{
    Brokers:          []string{"broker1:9093", "broker2:9093"},
    SecurityProtocol: "SASL_SSL",
    SASLMechanism:    "SCRAM-SHA-256",
    SASLUsername:     "kafka-user",
    SASLPassword:     "kafka-password",
}
```

## Error Handling

The library provides comprehensive error handling:

```go
err := producer.SendMessage(ctx, message)
if err != nil {
    switch {
    case errors.Is(err, context.DeadlineExceeded):
        // Handle timeout
        log.Printf("Message send timeout: %v", err)
    case errors.Is(err, context.Canceled):
        // Handle cancellation
        log.Printf("Message send canceled: %v", err)
    default:
        // Handle other errors
        log.Printf("Message send failed: %v", err)
    }
}
```

## Performance Tuning

### Producer Performance

```go
config := &port.ProducerConfig{
    Brokers:         []string{"localhost:9092"},
    RequiredAcks:    1,                    // Balance between performance and durability
    CompressionType: "lz4",               // Fast compression
    FlushFrequency:  10 * time.Millisecond, // Batch frequently
    FlushMessages:   1000,                // Large batches
    FlushBytes:      1024 * 1024,         // 1MB batches
}
```

### Consumer Performance

```go
config := &port.ConsumerConfig{
    Brokers:           []string{"localhost:9092"},
    GroupID:           "high-throughput-group",
    FetchMinBytes:     1024,              // Fetch at least 1KB
    FetchMaxBytes:     50 * 1024 * 1024,  // Fetch up to 50MB
    FetchMaxWait:      100 * time.Millisecond, // Don't wait too long
    EnableAutoCommit:  true,              // Reduce overhead
    AutoCommitInterval: 1000 * time.Millisecond,
}
```

## Testing

### Unit Tests

```bash
# Run unit tests
go test ./src/infrastructure/service/

# Run with coverage
go test -cover ./src/infrastructure/service/
```

### Integration Tests

```bash
# Start Kafka locally (requires Docker)
docker run -d --name kafka \
  -p 9092:9092 \
  -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  confluentinc/cp-kafka:latest

# Run integration tests
go test -tags=integration ./src/infrastructure/service/
```

### Benchmarks

```bash
# Run performance benchmarks
go test -bench=. ./src/infrastructure/service/

# Run with memory profiling
go test -bench=. -memprofile=mem.prof ./src/infrastructure/service/
```

## Monitoring and Observability

The library provides comprehensive logging using structured logging (Zap):

```go
logger, _ := zap.NewProduction()
// or for development
logger, _ := zap.NewDevelopment()

producer, err := service.NewKafkaProducer(config, logger)
```

Log levels and events:
- **INFO**: Connection events, topic operations, successful operations
- **DEBUG**: Message-level details, metadata operations
- **WARN**: Non-fatal errors, configuration issues
- **ERROR**: Connection failures, send failures, critical errors

## Best Practices

### Producer Best Practices

1. **Use appropriate acknowledgment levels**:
   - `RequiredAcks: 0` for maximum throughput (fire-and-forget)
   - `RequiredAcks: 1` for balanced performance and durability
   - `RequiredAcks: -1` for maximum durability

2. **Enable compression for large messages**:
   ```go
   CompressionType: "lz4" // Good balance of speed and compression ratio
   ```

3. **Use batching for high throughput**:
   ```go
   FlushMessages: 1000
   FlushBytes: 1024 * 1024 // 1MB
   ```

4. **Handle errors appropriately**:
   - Implement retry logic for transient errors
   - Use dead letter queues for permanent failures

### Consumer Best Practices

1. **Choose the right offset reset strategy**:
   - `"earliest"` to process all available messages
   - `"latest"` to process only new messages

2. **Optimize fetch settings**:
   ```go
   FetchMinBytes: 1024      // Reduce network roundtrips
   FetchMaxBytes: 50*1024*1024 // Allow large batches
   ```

3. **Handle rebalancing gracefully**:
   - Keep message processing fast
   - Implement idempotent processing

4. **Monitor consumer lag**:
   ```go
   // Use admin client to check consumer group offsets
   offsets, err := admin.ListConsumerGroupOffsets(ctx, groupID, topics)
   ```

### Admin Best Practices

1. **Use reasonable timeouts**:
   ```go
   RequestTimeout: 30 * time.Second
   ```

2. **Handle topic creation idempotently**:
   ```go
   err := admin.CreateTopic(ctx, topicConfig)
   if err != nil && !strings.Contains(err.Error(), "already exists") {
       return err
   }
   ```

3. **Monitor cluster health**:
   ```go
   metadata, err := admin.GetClusterMetadata(ctx)
   // Check broker count, topic count, etc.
   ```

## Troubleshooting

### Common Issues

1. **Connection Issues**:
   ```
   Error: "connection refused"
   Solution: Verify Kafka broker addresses and ports
   ```

2. **Authentication Failures**:
   ```
   Error: "SASL authentication failed"
   Solution: Check SASL credentials and mechanism
   ```

3. **Topic Not Found**:
   ```
   Error: "unknown topic or partition"
   Solution: Create topic first or enable auto-creation
   ```

4. **Consumer Group Rebalancing**:
   ```
   Error: "group coordinator not available"
   Solution: Wait for coordinator election or check broker health
   ```

### Debug Mode

Enable debug logging for detailed troubleshooting:

```go
logger, _ := zap.NewDevelopment()
logger = logger.With(zap.String("component", "kafka"))
```

## Migration Guide

### From confluent-kafka-go

```go
// Old (confluent-kafka-go)
producer, err := kafka.NewProducer(&kafka.ConfigMap{
    "bootstrap.servers": "localhost:9092",
})

// New (this library)
config := &port.ProducerConfig{
    Brokers: []string{"localhost:9092"},
}
producer, err := service.NewKafkaProducer(config, logger)
```

### From shopify/sarama

```go
// Old (direct sarama)
config := sarama.NewConfig()
producer, err := sarama.NewSyncProducer([]string{"localhost:9092"}, config)

// New (this library)
config := &port.ProducerConfig{
    Brokers: []string{"localhost:9092"},
}
producer, err := service.NewKafkaProducer(config, logger)
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Add tests for new functionality
4. Ensure all tests pass
5. Submit a pull request

## License

This library is released under the MIT License. See LICENSE file for details.

## Support

For issues and questions:
1. Check the troubleshooting section
2. Review existing GitHub issues
3. Create a new issue with detailed information

## Changelog

### v1.0.0
- ✅ Initial release with full producer, consumer, and admin functionality
- ✅ Support for all major Kafka features
- ✅ Comprehensive test suite
- ✅ Production-ready error handling and logging
- ✅ Security support (SASL/SSL)
- ✅ Performance optimizations
