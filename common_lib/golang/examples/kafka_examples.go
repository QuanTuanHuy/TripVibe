package examples

import (
	"context"
	"fmt"
	"log"
	"time"

	"github.com/quantuanhuy/lib/src/core/port"
	"github.com/quantuanhuy/lib/src/infrastructure/service"
	"go.uber.org/zap"
)

// SimpleProducerExample demonstrates basic message production
func SimpleProducerExample() {
	logger, _ := zap.NewDevelopment()
	defer logger.Sync()

	// Configure producer
	config := &port.ProducerConfig{
		Brokers:      []string{"localhost:9092"},
		RequiredAcks: 1,
		RetryMax:     3,
	}

	// Create producer
	producer, err := service.NewKafkaProducer(config, logger)
	if err != nil {
		log.Fatal("Failed to create producer:", err)
	}
	defer producer.Close()

	// Send a simple message
	message := &port.KafkaMessage{
		Topic: "simple-topic",
		Key:   []byte("user-123"),
		Value: []byte(`{"event": "user_login", "timestamp": "2024-01-01T10:00:00Z"}`),
		Headers: map[string][]byte{
			"content-type": []byte("application/json"),
			"source":       []byte("auth-service"),
		},
	}

	ctx := context.Background()
	err = producer.SendMessage(ctx, message)
	if err != nil {
		log.Fatal("Failed to send message:", err)
	}

	fmt.Printf("Message sent to partition %d, offset %d\n", message.Partition, message.Offset)
}

// HighThroughputProducerExample demonstrates optimized producer for high throughput
func HighThroughputProducerExample() {
	logger, _ := zap.NewProduction()
	defer logger.Sync()

	// High-throughput configuration
	config := &port.ProducerConfig{
		Brokers:         []string{"localhost:9092"},
		RequiredAcks:    1,                     // Balance durability and performance
		CompressionType: "lz4",                 // Fast compression
		FlushFrequency:  10 * time.Millisecond, // Batch every 10ms
		FlushMessages:   1000,                  // Or when we have 1000 messages
		FlushBytes:      1024 * 1024,           // Or when we have 1MB
		MaxMessageBytes: 10 * 1024 * 1024,      // 10MB max message size
		RetryMax:        3,
		RetryBackoff:    100 * time.Millisecond,
	}

	producer, err := service.NewKafkaProducer(config, logger)
	if err != nil {
		log.Fatal("Failed to create producer:", err)
	}
	defer producer.Close()

	ctx := context.Background()

	// Send many messages quickly
	for i := 0; i < 10000; i++ {
		message := &port.KafkaMessage{
			Topic: "high-throughput-topic",
			Key:   []byte(fmt.Sprintf("key-%d", i)),
			Value: []byte(fmt.Sprintf(`{"id": %d, "data": "some data here"}`, i)),
		}

		// Use async for maximum throughput
		err = producer.SendMessageAsync(ctx, message,
			func(msg *port.KafkaMessage) {
				// Success callback
				fmt.Printf("Message %s sent successfully\n", string(msg.Key))
			},
			func(msg *port.KafkaMessage, err error) {
				// Error callback
				log.Printf("Failed to send message %s: %v", string(msg.Key), err)
			})

		if err != nil {
			log.Printf("Failed to queue message %d: %v", i, err)
		}

		if i%1000 == 0 {
			fmt.Printf("Queued %d messages\n", i)
		}
	}

	// Flush any remaining messages
	producer.Flush(ctx)
	fmt.Println("All messages sent")
}

// TransactionalProducerExample demonstrates exactly-once semantics
func TransactionalProducerExample() {
	logger, _ := zap.NewDevelopment()
	defer logger.Sync()

	config := &port.ProducerConfig{
		Brokers:           []string{"localhost:9092"},
		RequiredAcks:      -1,   // All replicas must acknowledge
		EnableIdempotence: true, // Prevent duplicates
		TransactionID:     "order-processor-1",
		RetryMax:          5,
	}

	producer, err := service.NewKafkaProducer(config, logger)
	if err != nil {
		log.Fatal("Failed to create producer:", err)
	}
	defer producer.Close()

	ctx := context.Background()

	// Simulate processing an order with multiple events
	orderID := "order-12345"

	// Begin transaction
	err = producer.BeginTransaction(ctx)
	if err != nil {
		log.Fatal("Failed to begin transaction:", err)
	}

	// Send order created event
	orderCreated := &port.KafkaMessage{
		Topic: "order-events",
		Key:   []byte(orderID),
		Value: []byte(`{"event": "order_created", "order_id": "order-12345", "amount": 99.99}`),
	}

	err = producer.SendMessage(ctx, orderCreated)
	if err != nil {
		producer.AbortTransaction(ctx)
		log.Fatal("Failed to send order created event:", err)
	}

	// Send payment processed event
	paymentProcessed := &port.KafkaMessage{
		Topic: "payment-events",
		Key:   []byte(orderID),
		Value: []byte(`{"event": "payment_processed", "order_id": "order-12345", "amount": 99.99}`),
	}

	err = producer.SendMessage(ctx, paymentProcessed)
	if err != nil {
		producer.AbortTransaction(ctx)
		log.Fatal("Failed to send payment event:", err)
	}

	// Send inventory updated event
	inventoryUpdated := &port.KafkaMessage{
		Topic: "inventory-events",
		Key:   []byte("product-abc"),
		Value: []byte(`{"event": "inventory_updated", "product_id": "product-abc", "quantity": -1}`),
	}

	err = producer.SendMessage(ctx, inventoryUpdated)
	if err != nil {
		producer.AbortTransaction(ctx)
		log.Fatal("Failed to send inventory event:", err)
	}

	// Commit transaction - all messages sent atomically
	err = producer.CommitTransaction(ctx)
	if err != nil {
		log.Fatal("Failed to commit transaction:", err)
	}

	fmt.Printf("Order %s processed successfully with exactly-once guarantees\n", orderID)
}

// SimpleConsumerExample demonstrates basic message consumption
func SimpleConsumerExample() {
	logger, _ := zap.NewDevelopment()
	defer logger.Sync()

	config := &port.ConsumerConfig{
		Brokers:            []string{"localhost:9092"},
		GroupID:            "simple-consumer-group",
		AutoOffsetReset:    "earliest",
		EnableAutoCommit:   true,
		AutoCommitInterval: 1 * time.Second,
	}

	consumer, err := service.NewKafkaConsumer(config, logger)
	if err != nil {
		log.Fatal("Failed to create consumer:", err)
	}
	defer consumer.Close()

	// Message handler
	handler := func(message *port.KafkaMessage) error {
		fmt.Printf("Received message: key=%s, value=%s, partition=%d, offset=%d\n",
			string(message.Key), string(message.Value), message.Partition, message.Offset)

		// Process the message here
		// Return error if processing fails to trigger retry/dead letter handling
		return nil
	}

	// Start consuming
	ctx := context.Background()
	topics := []string{"simple-topic"}

	fmt.Println("Starting consumer...")
	err = consumer.Subscribe(ctx, topics, handler)
	if err != nil {
		log.Fatal("Failed to start consuming:", err)
	}
}

// ManualCommitConsumerExample demonstrates manual offset management
func ManualCommitConsumerExample() {
	logger, _ := zap.NewDevelopment()
	defer logger.Sync()

	config := &port.ConsumerConfig{
		Brokers:           []string{"localhost:9092"},
		GroupID:           "manual-commit-group",
		AutoOffsetReset:   "earliest",
		EnableAutoCommit:  false, // Manual commit for exactly-once processing
		SessionTimeout:    30 * time.Second,
		HeartbeatInterval: 3 * time.Second,
	}

	consumer, err := service.NewKafkaConsumer(config, logger)
	if err != nil {
		log.Fatal("Failed to create consumer:", err)
	}
	defer consumer.Close()

	// Message handler with manual commit
	handler := func(message *port.KafkaMessage) error {
		fmt.Printf("Processing message: %s\n", string(message.Value))

		// Simulate processing (e.g., database write, API call)
		if err := processBusinessLogic(message); err != nil {
			// Don't commit offset on processing failure
			return fmt.Errorf("failed to process message: %w", err)
		}

		// Manually commit offset after successful processing
		offsets := map[string]map[int32]int64{
			message.Topic: {
				message.Partition: message.Offset + 1,
			},
		}

		ctx := context.Background()
		if err := consumer.CommitOffsets(ctx, offsets); err != nil {
			return fmt.Errorf("failed to commit offset: %w", err)
		}

		fmt.Printf("Successfully processed and committed message at offset %d\n", message.Offset)
		return nil
	}

	ctx := context.Background()
	topics := []string{"critical-topic"}

	fmt.Println("Starting consumer with manual commits...")
	err = consumer.Subscribe(ctx, topics, handler)
	if err != nil {
		log.Fatal("Failed to start consuming:", err)
	}
}

// BatchConsumerExample demonstrates high-throughput batch processing
func BatchConsumerExample() {
	logger, _ := zap.NewProduction()
	defer logger.Sync()

	config := &port.ConsumerConfig{
		Brokers:           []string{"localhost:9092"},
		GroupID:           "batch-processor-group",
		AutoOffsetReset:   "earliest",
		EnableAutoCommit:  false,
		FetchMinBytes:     1024,             // Fetch at least 1KB
		FetchMaxBytes:     50 * 1024 * 1024, // Fetch up to 50MB
		FetchMaxWait:      100 * time.Millisecond,
		SessionTimeout:    30 * time.Second,
		HeartbeatInterval: 3 * time.Second,
	}

	consumer, err := service.NewKafkaConsumer(config, logger)
	if err != nil {
		log.Fatal("Failed to create consumer:", err)
	}
	defer consumer.Close()

	// Batch processing with buffer
	var batch []*port.KafkaMessage
	batchSize := 100
	lastCommit := time.Now()
	commitInterval := 5 * time.Second

	handler := func(message *port.KafkaMessage) error {
		// Add to batch
		batch = append(batch, message)

		// Process batch when full or after timeout
		if len(batch) >= batchSize || time.Since(lastCommit) >= commitInterval {
			if err := processBatch(batch); err != nil {
				return fmt.Errorf("failed to process batch: %w", err)
			}

			// Commit the highest offset in the batch
			highestOffsets := make(map[string]map[int32]int64)
			for _, msg := range batch {
				if highestOffsets[msg.Topic] == nil {
					highestOffsets[msg.Topic] = make(map[int32]int64)
				}
				if highestOffsets[msg.Topic][msg.Partition] < msg.Offset+1 {
					highestOffsets[msg.Topic][msg.Partition] = msg.Offset + 1
				}
			}

			ctx := context.Background()
			if err := consumer.CommitOffsets(ctx, highestOffsets); err != nil {
				return fmt.Errorf("failed to commit batch offsets: %w", err)
			}

			fmt.Printf("Processed and committed batch of %d messages\n", len(batch))
			batch = batch[:0] // Clear batch
			lastCommit = time.Now()
		}

		return nil
	}

	ctx := context.Background()
	topics := []string{"high-volume-topic"}

	fmt.Println("Starting batch consumer...")
	err = consumer.Subscribe(ctx, topics, handler)
	if err != nil {
		log.Fatal("Failed to start consuming:", err)
	}
}

// AdminOperationsExample demonstrates topic and consumer group management
func AdminOperationsExample() {
	logger, _ := zap.NewDevelopment()
	defer logger.Sync()

	config := &port.AdminConfig{
		Brokers:        []string{"localhost:9092"},
		RequestTimeout: 30 * time.Second,
		RetryMax:       3,
		RetryBackoff:   1 * time.Second,
	}

	admin, err := service.NewKafkaAdmin(config, logger)
	if err != nil {
		log.Fatal("Failed to create admin client:", err)
	}
	defer admin.Close()

	ctx := context.Background()

	// Create a topic
	topicConfig := &port.TopicConfig{
		Name:              "user-events",
		NumPartitions:     6,
		ReplicationFactor: 1,
		ConfigEntries: map[string]*string{
			"retention.ms":     stringPtr("604800000"), // 7 days
			"compression.type": stringPtr("lz4"),
			"cleanup.policy":   stringPtr("delete"),
		},
	}

	fmt.Println("Creating topic...")
	err = admin.CreateTopic(ctx, topicConfig)
	if err != nil {
		log.Printf("Failed to create topic (may already exist): %v", err)
	} else {
		fmt.Printf("Topic %s created successfully\n", topicConfig.Name)
	}

	// List all topics
	fmt.Println("\nListing topics...")
	topics, err := admin.ListTopics(ctx)
	if err != nil {
		log.Fatal("Failed to list topics:", err)
	}

	for _, topic := range topics {
		fmt.Printf("- %s\n", topic)
	}

	// Describe a topic
	fmt.Println("\nDescribing topic...")
	topicDetails, err := admin.DescribeTopic(ctx, "user-events")
	if err != nil {
		log.Printf("Failed to describe topic: %v", err)
	} else {
		fmt.Printf("Topic: %s, Partitions: %d\n", topicDetails.Name, len(topicDetails.Partitions))
		for _, partition := range topicDetails.Partitions {
			fmt.Printf("  Partition %d: Leader=%d, Replicas=%v\n",
				partition.Partition, partition.Leader, partition.Replicas)
		}
	}

	// List consumer groups
	fmt.Println("\nListing consumer groups...")
	groups, err := admin.ListConsumerGroups(ctx)
	if err != nil {
		log.Fatal("Failed to list consumer groups:", err)
	}

	for _, groupID := range groups {
		fmt.Printf("- %s\n", groupID)
	}

	// Describe a consumer group
	if len(groups) > 0 {
		firstGroup := groups[0]

		fmt.Printf("\nDescribing consumer group: %s\n", firstGroup)
		groupDetails, err := admin.DescribeConsumerGroup(ctx, firstGroup)
		if err != nil {
			log.Printf("Failed to describe consumer group: %v", err)
		} else {
			fmt.Printf("Group: %s, State: %s, Protocol: %s\n",
				groupDetails.GroupID, groupDetails.State, groupDetails.ProtocolType)
			fmt.Printf("Members: %d\n", len(groupDetails.Members))
		}

		// Get consumer group offsets
		fmt.Println("\nGetting consumer group offsets...")
		offsets, err := admin.ListConsumerGroupOffsets(ctx, firstGroup, []string{})
		if err != nil {
			log.Printf("Failed to get consumer group offsets: %v", err)
		} else {
			for topic, partitions := range offsets {
				fmt.Printf("Topic: %s\n", topic)
				for partition, offset := range partitions {
					fmt.Printf("  Partition %d: Offset %d\n", partition, offset)
				}
			}
		}
	}

	// Add partitions to existing topic
	fmt.Println("\nAdding partitions to topic...")
	err = admin.CreatePartitions(ctx, "user-events", 10)
	if err != nil {
		log.Printf("Failed to add partitions: %v", err)
	} else {
		fmt.Println("Partitions added successfully")
	}

	// Get cluster metadata
	fmt.Println("\nGetting cluster metadata...")
	metadata, err := admin.GetClusterMetadata(ctx)
	if err != nil {
		log.Fatal("Failed to get cluster metadata:", err)
	}

	fmt.Printf("Available Topics: %d\n", len(metadata.Topics))
	fmt.Printf("Brokers: %d\n", len(metadata.Brokers))
	for _, broker := range metadata.Brokers {
		fmt.Printf("  Broker %d: %s\n", broker.ID, broker.Host)
	}
}

// ErrorHandlingExample demonstrates proper error handling patterns
func ErrorHandlingExample() {
	logger, _ := zap.NewDevelopment()
	defer logger.Sync()

	config := &port.ProducerConfig{
		Brokers:      []string{"localhost:9092"},
		RequiredAcks: 1,
		RetryMax:     3,
		RetryBackoff: 100 * time.Millisecond,
	}

	producer, err := service.NewKafkaProducer(config, logger)
	if err != nil {
		log.Fatal("Failed to create producer:", err)
	}
	defer producer.Close()

	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()

	message := &port.KafkaMessage{
		Topic: "test-topic",
		Key:   []byte("test-key"),
		Value: []byte("test message"),
	}

	// Demonstrate different error scenarios
	err = producer.SendMessage(ctx, message)
	if err != nil {
		switch {
		case ctx.Err() == context.DeadlineExceeded:
			// Handle timeout - maybe retry with exponential backoff
			fmt.Printf("Message send timeout, implementing retry logic: %v\n", err)

			for retry := 0; retry < 3; retry++ {
				backoff := time.Duration(retry+1) * time.Second
				fmt.Printf("Retrying in %v...\n", backoff)
				time.Sleep(backoff)

				retryCtx, retryCancel := context.WithTimeout(context.Background(), 10*time.Second)
				err = producer.SendMessage(retryCtx, message)
				retryCancel()

				if err == nil {
					fmt.Println("Message sent successfully on retry")
					break
				}
			}

		case ctx.Err() == context.Canceled:
			// Handle cancellation - maybe store for later retry
			fmt.Printf("Message send canceled, storing for later: %v\n", err)
			storeForLaterRetry(message)

		default:
			// Handle other Kafka errors
			fmt.Printf("Kafka error occurred: %v\n", err)

			// Check if it's a recoverable error
			if isRecoverableError(err) {
				fmt.Println("Error is recoverable, will retry")
				// Implement retry logic
			} else {
				fmt.Println("Error is not recoverable, sending to dead letter queue")
				sendToDeadLetterQueue(message, err)
			}
		}
	} else {
		fmt.Printf("Message sent successfully to partition %d, offset %d\n",
			message.Partition, message.Offset)
	}
}

// SecurityExample demonstrates SASL/SSL configuration
func SecurityExample() {
	logger, _ := zap.NewProduction()
	defer logger.Sync()

	// SASL/SSL configuration
	config := &port.ProducerConfig{
		Brokers:           []string{"broker1:9093", "broker2:9093", "broker3:9093"},
		SecurityProtocol:  "SASL_SSL",
		SASLMechanism:     "SCRAM-SHA-256",
		SASLUsername:      "kafka-user",
		SASLPassword:      "kafka-password",
		RequiredAcks:      -1, // All replicas for security
		EnableIdempotence: true,
	}

	producer, err := service.NewKafkaProducer(config, logger)
	if err != nil {
		log.Fatal("Failed to create secure producer:", err)
	}
	defer producer.Close()

	message := &port.KafkaMessage{
		Topic: "secure-topic",
		Key:   []byte("secure-key"),
		Value: []byte("sensitive data"),
		Headers: map[string][]byte{
			"encryption": []byte("aes-256"),
			"source":     []byte("secure-service"),
		},
	}

	ctx := context.Background()
	err = producer.SendMessage(ctx, message)
	if err != nil {
		log.Fatal("Failed to send secure message:", err)
	}

	fmt.Println("Secure message sent successfully")
}

// Helper functions for examples

func stringPtr(s string) *string {
	return &s
}

func processBusinessLogic(message *port.KafkaMessage) error {
	// Simulate business logic processing
	time.Sleep(10 * time.Millisecond)

	// Simulate occasional failures
	// if rand.Float32() < 0.1 { // 10% failure rate
	//     return errors.New("simulated processing error")
	// }

	return nil
}

func processBatch(messages []*port.KafkaMessage) error {
	fmt.Printf("Processing batch of %d messages\n", len(messages))

	// Simulate batch processing
	time.Sleep(time.Duration(len(messages)) * time.Millisecond)

	return nil
}

func storeForLaterRetry(message *port.KafkaMessage) {
	// Store message in local queue, database, or file for later retry
	fmt.Printf("Storing message for later retry: %s\n", string(message.Key))
}

func sendToDeadLetterQueue(message *port.KafkaMessage, err error) {
	// Send to dead letter topic for manual investigation
	fmt.Printf("Sending message to dead letter queue: %s, error: %v\n", string(message.Key), err)
}

func isRecoverableError(err error) bool {
	// Determine if error is recoverable based on error type/message
	errorStr := err.Error()

	// These are typically recoverable
	recoverableErrors := []string{
		"connection refused",
		"timeout",
		"leader not available",
		"not enough replicas",
	}

	for _, recoverable := range recoverableErrors {
		if contains(errorStr, recoverable) {
			return true
		}
	}

	return false
}

func contains(s, substr string) bool {
	return len(s) >= len(substr) &&
		(s == substr ||
			(len(s) > len(substr) &&
				(s[:len(substr)] == substr ||
					s[len(s)-len(substr):] == substr ||
					containsSubstring(s, substr))))
}

func containsSubstring(s, substr string) bool {
	for i := 0; i <= len(s)-len(substr); i++ {
		if s[i:i+len(substr)] == substr {
			return true
		}
	}
	return false
}
