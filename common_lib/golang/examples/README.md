# Kafka Library Examples

This directory contains comprehensive examples demonstrating how to use the Kafka library for various use cases.

## Examples Overview

### Producer Examples

1. **Simple Producer** (`SimpleProducerExample`)
   - Basic message production with headers
   - Synchronous sending
   - Error handling

2. **High Throughput Producer** (`HighThroughputProducerExample`)
   - Optimized configuration for maximum throughput
   - Asynchronous sending with callbacks
   - Batching and compression

3. **Transactional Producer** (`TransactionalProducerExample`)
   - Exactly-once semantics with transactions
   - Atomic multi-topic operations
   - Transaction commit/abort handling

### Consumer Examples

1. **Simple Consumer** (`SimpleConsumerExample`)
   - Basic message consumption
   - Automatic offset commits
   - Message processing

2. **Manual Commit Consumer** (`ManualCommitConsumerExample`)
   - Manual offset management for exactly-once processing
   - Error handling with retry logic
   - Fine-grained offset control

3. **Batch Consumer** (`BatchConsumerExample`)
   - High-throughput batch processing
   - Configurable batch sizes
   - Optimized for bulk operations

### Admin Examples

1. **Admin Operations** (`AdminOperationsExample`)
   - Topic creation and management
   - Consumer group operations
   - Cluster metadata retrieval
   - Partition management

### Security and Error Handling

1. **Security Example** (`SecurityExample`)
   - SASL/SSL authentication
   - Secure message transmission
   - Production security configuration

2. **Error Handling** (`ErrorHandlingExample`)
   - Comprehensive error handling patterns
   - Retry logic implementation
   - Dead letter queue patterns

## Running Examples

### Prerequisites

1. **Running Kafka Cluster**
   ```bash
   # Using Docker Compose (recommended for testing)
   docker-compose up -d
   
   # Or using Docker directly
   docker run -d --name zookeeper \
     -p 2181:2181 \
     confluentinc/cp-zookeeper:latest \
     -e ZOOKEEPER_CLIENT_PORT=2181

   docker run -d --name kafka \
     -p 9092:9092 \
     --link zookeeper \
     confluentinc/cp-kafka:latest \
     -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 \
     -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092
   ```

2. **Go Dependencies**
   ```bash
   go mod tidy
   ```

### Running Individual Examples

```go
package main

import "github.com/quantuanhuy/lib/examples"

func main() {
    // Run producer examples
    examples.SimpleProducerExample()
    examples.HighThroughputProducerExample()
    examples.TransactionalProducerExample()
    
    // Run consumer examples (in separate processes)
    examples.SimpleConsumerExample()
    examples.ManualCommitConsumerExample()
    examples.BatchConsumerExample()
    
    // Run admin examples
    examples.AdminOperationsExample()
    
    // Run security and error handling examples
    examples.SecurityExample()
    examples.ErrorHandlingExample()
}
```

### Example Scenarios

#### Scenario 1: E-commerce Order Processing

```go
// Use transactional producer for order processing
examples.TransactionalProducerExample()

// Use manual commit consumer for payment processing
examples.ManualCommitConsumerExample()
```

#### Scenario 2: High-Volume Event Streaming

```go
// Use high-throughput producer for event ingestion
examples.HighThroughputProducerExample()

// Use batch consumer for analytics processing
examples.BatchConsumerExample()
```

#### Scenario 3: Microservices Communication

```go
// Use simple producer for service-to-service messaging
examples.SimpleProducerExample()

// Use simple consumer for event handling
examples.SimpleConsumerExample()
```

## Best Practices Demonstrated

### Producer Best Practices

1. **Use appropriate acknowledgment levels**
   - `RequiredAcks: 0` for fire-and-forget scenarios
   - `RequiredAcks: 1` for balanced performance and durability
   - `RequiredAcks: -1` for critical data requiring all replica acknowledgments

2. **Enable compression for large messages**
   - LZ4 for speed
   - GZIP for compression ratio
   - Snappy for balanced performance

3. **Implement proper batching**
   - Configure flush settings based on throughput requirements
   - Use async sending for high throughput scenarios

4. **Handle errors gracefully**
   - Implement retry logic for transient errors
   - Use dead letter queues for permanent failures

### Consumer Best Practices

1. **Choose appropriate offset management**
   - Auto-commit for simple scenarios
   - Manual commit for exactly-once processing

2. **Optimize fetch settings**
   - Adjust `FetchMinBytes` and `FetchMaxBytes` for your workload
   - Use `FetchMaxWait` to balance latency and throughput

3. **Handle rebalancing**
   - Keep message processing fast
   - Implement idempotent processing logic

4. **Monitor consumer lag**
   - Use admin client to track offset progress
   - Implement alerting for high lag

### Admin Best Practices

1. **Use reasonable timeouts**
   - Set appropriate `RequestTimeout` for operations
   - Account for network latency in distributed environments

2. **Handle resource creation idempotently**
   - Check for existence before creating topics
   - Handle "already exists" errors gracefully

3. **Monitor cluster health**
   - Regularly check broker availability
   - Monitor topic and partition distribution

## Configuration Examples

### Development Configuration

```go
// Producer for development
config := &port.ProducerConfig{
    Brokers:      []string{"localhost:9092"},
    RequiredAcks: 1,
    RetryMax:     3,
}

// Consumer for development
config := &port.ConsumerConfig{
    Brokers:         []string{"localhost:9092"},
    GroupID:         "dev-group",
    AutoOffsetReset: "earliest",
    EnableAutoCommit: true,
}
```

### Production Configuration

```go
// Producer for production
config := &port.ProducerConfig{
    Brokers:           []string{"broker1:9092", "broker2:9092", "broker3:9092"},
    RequiredAcks:      -1,                    // All replicas
    EnableIdempotence: true,                  // Exactly-once
    CompressionType:   "lz4",                 // Efficient compression
    RetryMax:          5,                     // More retries
    SecurityProtocol:  "SASL_SSL",           // Secure connection
    SASLMechanism:     "SCRAM-SHA-256",      // Strong authentication
}

// Consumer for production
config := &port.ConsumerConfig{
    Brokers:           []string{"broker1:9092", "broker2:9092", "broker3:9092"},
    GroupID:           "prod-consumer-group",
    AutoOffsetReset:   "latest",              // Don't reprocess old messages
    EnableAutoCommit:  false,                 // Manual commit for reliability
    SessionTimeout:    30 * time.Second,     // Longer timeout for stability
    FetchMaxBytes:     50 * 1024 * 1024,     // 50MB for high throughput
    SecurityProtocol:  "SASL_SSL",           // Secure connection
    SASLMechanism:     "SCRAM-SHA-256",      // Strong authentication
}
```

## Troubleshooting

### Common Issues

1. **Connection refused**
   ```
   Error: dial tcp [::1]:9092: connect: connection refused
   Solution: Ensure Kafka is running and accessible at the specified address
   ```

2. **Topic not found**
   ```
   Error: kafka: client has run out of available brokers to talk to
   Solution: Create topics first or enable auto-creation in Kafka
   ```

3. **Authentication failed**
   ```
   Error: kafka: client authentication failed
   Solution: Check SASL credentials and mechanism configuration
   ```

4. **Consumer group rebalancing**
   ```
   Error: kafka: error while consuming
   Solution: Ensure session.timeout.ms > heartbeat.interval.ms * 3
   ```

### Debug Tips

1. **Enable debug logging**
   ```go
   logger, _ := zap.NewDevelopment()
   logger = logger.With(zap.String("component", "kafka"))
   ```

2. **Check Kafka cluster health**
   ```bash
   # List topics
   kafka-topics.sh --bootstrap-server localhost:9092 --list
   
   # Describe consumer groups
   kafka-consumer-groups.sh --bootstrap-server localhost:9092 --list
   ```

3. **Monitor consumer lag**
   ```bash
   kafka-consumer-groups.sh --bootstrap-server localhost:9092 \
     --group my-group --describe
   ```

## Performance Testing

The examples include benchmark functions that can be used for performance testing:

```bash
# Run producer benchmarks
go test -bench=BenchmarkProducer ./examples/

# Run consumer benchmarks  
go test -bench=BenchmarkConsumer ./examples/

# Run with memory profiling
go test -bench=. -memprofile=mem.prof ./examples/
```

## Contributing

When adding new examples:

1. Follow the existing naming convention
2. Include comprehensive error handling
3. Add comments explaining the key concepts
4. Update this README with the new example
5. Ensure examples work with the provided Docker setup

## Additional Resources

- [Kafka Documentation](https://kafka.apache.org/documentation/)
- [Sarama Library Documentation](https://pkg.go.dev/github.com/IBM/sarama)
- [Kafka Best Practices](https://kafka.apache.org/documentation/#bestpractices)
- [Main Library Documentation](../KAFKA_DOCUMENTATION.md)
