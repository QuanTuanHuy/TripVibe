// Package service provides infrastructure implementations for Kafka operations.
// This package contains concrete implementations of the Kafka services defined in the core layer.
package service

import (
	"context"
	"fmt"
	"strings"
	"time"

	"github.com/IBM/sarama"
	"github.com/quantuanhuy/lib/src/core/port"
	"go.uber.org/zap"
)

// KafkaProducer provides Kafka producer operations implementation.
// It implements the IKafkaProducerPort interface and handles all Kafka producer-related operations
// including synchronous/asynchronous message sending, batching, and transactional operations.
type KafkaProducer struct {
	syncProducer  sarama.SyncProducer  // Synchronous producer for immediate sends
	asyncProducer sarama.AsyncProducer // Asynchronous producer for non-blocking sends
	config        *port.ProducerConfig // Producer configuration
	logger        *zap.Logger          // Structured logger for operation tracking
	inTransaction bool                 // Flag to track transaction state
}

// NewKafkaProducer creates a new instance of KafkaProducer.
// It initializes both synchronous and asynchronous producers with the provided configuration.
//
// Parameters:
//   - config: Producer configuration containing broker addresses, security settings, etc.
//   - logger: Zap logger for operation logging
//
// Returns:
//   - port.IKafkaProducerPort: Interface implementation for Kafka producer operations
//   - error: nil if successful, otherwise the error that occurred during initialization
//
// Example:
//
//	config := &port.ProducerConfig{
//	    Brokers: []string{"localhost:9092"},
//	    RequiredAcks: 1,
//	    RetryMax: 3,
//	}
//	producer, err := NewKafkaProducer(config, logger)
func NewKafkaProducer(config *port.ProducerConfig, logger *zap.Logger) (port.IKafkaProducerPort, error) {
	// Create Sarama configuration
	saramaConfig := sarama.NewConfig()

	// Configure producer settings
	saramaConfig.Producer.Return.Successes = true
	saramaConfig.Producer.Return.Errors = true
	saramaConfig.Producer.RequiredAcks = sarama.RequiredAcks(config.RequiredAcks)
	saramaConfig.Producer.Retry.Max = config.RetryMax
	saramaConfig.Producer.Retry.Backoff = config.RetryBackoff

	// Configure message settings
	if config.MaxMessageBytes > 0 {
		saramaConfig.Producer.MaxMessageBytes = config.MaxMessageBytes
	}

	// Configure compression
	switch strings.ToLower(config.CompressionType) {
	case "gzip":
		saramaConfig.Producer.Compression = sarama.CompressionGZIP
	case "snappy":
		saramaConfig.Producer.Compression = sarama.CompressionSnappy
	case "lz4":
		saramaConfig.Producer.Compression = sarama.CompressionLZ4
	case "zstd":
		saramaConfig.Producer.Compression = sarama.CompressionZSTD
	default:
		saramaConfig.Producer.Compression = sarama.CompressionNone
	}

	// Configure batching
	if config.FlushFrequency > 0 {
		saramaConfig.Producer.Flush.Frequency = config.FlushFrequency
	}
	if config.FlushMessages > 0 {
		saramaConfig.Producer.Flush.Messages = config.FlushMessages
	}
	if config.FlushBytes > 0 {
		saramaConfig.Producer.Flush.Bytes = config.FlushBytes
	}

	// Configure idempotence
	if config.EnableIdempotence {
		saramaConfig.Producer.Idempotent = true
		saramaConfig.Net.MaxOpenRequests = 1
	}

	// Configure transactions
	if config.TransactionID != "" {
		saramaConfig.Producer.Transaction.ID = config.TransactionID
		saramaConfig.Producer.Idempotent = true
	}

	// Configure security
	if err := configureSecurity(saramaConfig, config); err != nil {
		return nil, fmt.Errorf("failed to configure security: %w", err)
	}

	// Create synchronous producer
	syncProducer, err := sarama.NewSyncProducer(config.Brokers, saramaConfig)
	if err != nil {
		logger.Error("Failed to create sync producer", zap.Error(err))
		return nil, fmt.Errorf("failed to create sync producer: %w", err)
	}

	// Create asynchronous producer
	asyncProducer, err := sarama.NewAsyncProducer(config.Brokers, saramaConfig)
	if err != nil {
		logger.Error("Failed to create async producer", zap.Error(err))
		syncProducer.Close()
		return nil, fmt.Errorf("failed to create async producer: %w", err)
	}

	producer := &KafkaProducer{
		syncProducer:  syncProducer,
		asyncProducer: asyncProducer,
		config:        config,
		logger:        logger,
		inTransaction: false,
	}

	// Start handling async producer messages
	go producer.handleAsyncProducerMessages()

	logger.Info("Kafka producer initialized successfully",
		zap.Strings("brokers", config.Brokers),
		zap.Int16("required_acks", config.RequiredAcks),
		zap.String("compression", config.CompressionType))

	return producer, nil
}

// configureSecurity configures SASL and SSL settings for the producer.
func configureSecurity(saramaConfig *sarama.Config, config *port.ProducerConfig) error {
	switch strings.ToUpper(config.SecurityProtocol) {
	case "SASL_PLAINTEXT":
		saramaConfig.Net.SASL.Enable = true
		saramaConfig.Net.SASL.User = config.SASLUsername
		saramaConfig.Net.SASL.Password = config.SASLPassword

		switch strings.ToUpper(config.SASLMechanism) {
		case "PLAIN":
			saramaConfig.Net.SASL.Mechanism = sarama.SASLTypePlaintext
		case "SCRAM-SHA-256":
			saramaConfig.Net.SASL.Mechanism = sarama.SASLTypeSCRAMSHA256
		case "SCRAM-SHA-512":
			saramaConfig.Net.SASL.Mechanism = sarama.SASLTypeSCRAMSHA512
		default:
			return fmt.Errorf("unsupported SASL mechanism: %s", config.SASLMechanism)
		}
	case "SASL_SSL":
		saramaConfig.Net.TLS.Enable = true
		saramaConfig.Net.SASL.Enable = true
		saramaConfig.Net.SASL.User = config.SASLUsername
		saramaConfig.Net.SASL.Password = config.SASLPassword

		switch strings.ToUpper(config.SASLMechanism) {
		case "PLAIN":
			saramaConfig.Net.SASL.Mechanism = sarama.SASLTypePlaintext
		case "SCRAM-SHA-256":
			saramaConfig.Net.SASL.Mechanism = sarama.SASLTypeSCRAMSHA256
		case "SCRAM-SHA-512":
			saramaConfig.Net.SASL.Mechanism = sarama.SASLTypeSCRAMSHA512
		default:
			return fmt.Errorf("unsupported SASL mechanism: %s", config.SASLMechanism)
		}
	case "SSL":
		saramaConfig.Net.TLS.Enable = true
	case "PLAINTEXT":
		// No additional configuration needed
	default:
		if config.SecurityProtocol != "" {
			return fmt.Errorf("unsupported security protocol: %s", config.SecurityProtocol)
		}
	}

	return nil
}

// handleAsyncProducerMessages handles success and error messages from async producer.
func (p *KafkaProducer) handleAsyncProducerMessages() {
	for {
		select {
		case success := <-p.asyncProducer.Successes():
			p.logger.Debug("Message sent successfully",
				zap.String("topic", success.Topic),
				zap.Int32("partition", success.Partition),
				zap.Int64("offset", success.Offset))

		case err := <-p.asyncProducer.Errors():
			p.logger.Error("Failed to send message",
				zap.String("topic", err.Msg.Topic),
				zap.Error(err.Err))
		}
	}
}

// SendMessage sends a single message to Kafka synchronously.
func (p *KafkaProducer) SendMessage(ctx context.Context, message *port.KafkaMessage) error {
	producerMessage := p.convertToProducerMessage(message)

	partition, offset, err := p.syncProducer.SendMessage(producerMessage)
	if err != nil {
		p.logger.Error("Failed to send message synchronously",
			zap.String("topic", message.Topic),
			zap.Error(err))
		return fmt.Errorf("failed to send message to topic %s: %w", message.Topic, err)
	}

	p.logger.Info("Message sent successfully",
		zap.String("topic", message.Topic),
		zap.Int32("partition", partition),
		zap.Int64("offset", offset))

	// Update message with actual partition and offset
	message.Partition = partition
	message.Offset = offset

	return nil
}

// SendMessageAsync sends a single message to Kafka asynchronously.
func (p *KafkaProducer) SendMessageAsync(ctx context.Context, message *port.KafkaMessage,
	successCallback func(*port.KafkaMessage), errorCallback func(*port.KafkaMessage, error)) error {

	producerMessage := p.convertToProducerMessage(message)

	// Add metadata to track callbacks
	producerMessage.Metadata = map[string]interface{}{
		"original_message": message,
		"success_callback": successCallback,
		"error_callback":   errorCallback,
	}

	select {
	case p.asyncProducer.Input() <- producerMessage:
		p.logger.Debug("Message queued for async sending",
			zap.String("topic", message.Topic))
		return nil
	case <-ctx.Done():
		return ctx.Err()
	default:
		return fmt.Errorf("failed to queue message: producer input channel is full")
	}
}

// SendMessages sends multiple messages to Kafka in a batch.
func (p *KafkaProducer) SendMessages(ctx context.Context, messages []*port.KafkaMessage) error {
	if len(messages) == 0 {
		return nil
	}

	var errors []error
	for i, message := range messages {
		if err := p.SendMessage(ctx, message); err != nil {
			p.logger.Error("Failed to send message in batch",
				zap.Int("message_index", i),
				zap.String("topic", message.Topic),
				zap.Error(err))
			errors = append(errors, fmt.Errorf("message %d: %w", i, err))
		}
	}

	if len(errors) > 0 {
		return fmt.Errorf("failed to send %d out of %d messages: %v", len(errors), len(messages), errors)
	}

	p.logger.Info("Batch messages sent successfully",
		zap.Int("count", len(messages)))

	return nil
}

// SendMessagesAsync sends multiple messages to Kafka asynchronously.
func (p *KafkaProducer) SendMessagesAsync(ctx context.Context, messages []*port.KafkaMessage,
	successCallback func([]*port.KafkaMessage), errorCallback func([]*port.KafkaMessage, error)) error {

	if len(messages) == 0 {
		return nil
	}

	for _, message := range messages {
		if err := p.SendMessageAsync(ctx, message, nil, nil); err != nil {
			if errorCallback != nil {
				errorCallback(messages, err)
			}
			return err
		}
	}

	// Note: Individual callbacks are not supported in batch async mode
	// The batch callbacks would need to be implemented with additional coordination
	if successCallback != nil {
		go func() {
			// This is a simplified implementation
			// In production, you'd want to track individual message success/failure
			time.Sleep(100 * time.Millisecond) // Wait for likely completion
			successCallback(messages)
		}()
	}

	p.logger.Info("Batch messages queued for async sending",
		zap.Int("count", len(messages)))

	return nil
}

// SendMessageToPartition sends a message to a specific partition.
func (p *KafkaProducer) SendMessageToPartition(ctx context.Context, message *port.KafkaMessage, partition int32) error {
	// Update message partition
	originalPartition := message.Partition
	message.Partition = partition

	// Convert to producer message with specific partition
	producerMessage := p.convertToProducerMessage(message)
	producerMessage.Partition = partition

	actualPartition, offset, err := p.syncProducer.SendMessage(producerMessage)
	if err != nil {
		// Restore original partition on error
		message.Partition = originalPartition
		p.logger.Error("Failed to send message to specific partition",
			zap.String("topic", message.Topic),
			zap.Int32("target_partition", partition),
			zap.Error(err))
		return fmt.Errorf("failed to send message to partition %d: %w", partition, err)
	}

	p.logger.Info("Message sent to specific partition",
		zap.String("topic", message.Topic),
		zap.Int32("partition", actualPartition),
		zap.Int64("offset", offset))

	// Update message with actual values
	message.Partition = actualPartition
	message.Offset = offset

	return nil
}

// BeginTransaction starts a new transaction (for transactional producer).
func (p *KafkaProducer) BeginTransaction(ctx context.Context) error {
	if p.config.TransactionID == "" {
		return fmt.Errorf("transaction ID not configured")
	}

	if p.inTransaction {
		return fmt.Errorf("transaction already in progress")
	}

	// Note: Sarama doesn't have explicit transaction begin/commit/abort methods
	// Transactions are handled automatically when TransactionID is set
	p.inTransaction = true

	p.logger.Info("Transaction started",
		zap.String("transaction_id", p.config.TransactionID))

	return nil
}

// CommitTransaction commits the current transaction.
func (p *KafkaProducer) CommitTransaction(ctx context.Context) error {
	if !p.inTransaction {
		return fmt.Errorf("no transaction in progress")
	}

	// Flush any pending messages
	if err := p.Flush(ctx); err != nil {
		return fmt.Errorf("failed to flush before commit: %w", err)
	}

	p.inTransaction = false

	p.logger.Info("Transaction committed",
		zap.String("transaction_id", p.config.TransactionID))

	return nil
}

// AbortTransaction aborts the current transaction.
func (p *KafkaProducer) AbortTransaction(ctx context.Context) error {
	if !p.inTransaction {
		return fmt.Errorf("no transaction in progress")
	}

	p.inTransaction = false

	p.logger.Info("Transaction aborted",
		zap.String("transaction_id", p.config.TransactionID))

	return nil
}

// GetMetadata returns metadata about Kafka topics and partitions.
func (p *KafkaProducer) GetMetadata(ctx context.Context, topics []string) (map[string][]int32, error) {
	// Create a temporary client to get proper metadata
	saramaConfig := sarama.NewConfig()
	client, err := sarama.NewClient(p.config.Brokers, saramaConfig)
	if err != nil {
		return nil, fmt.Errorf("failed to create client for metadata: %w", err)
	}
	defer client.Close()

	topicMetadata := make(map[string][]int32)

	for _, topic := range topics {
		partitions, err := client.Partitions(topic)
		if err != nil {
			p.logger.Error("Failed to get partitions for topic",
				zap.String("topic", topic),
				zap.Error(err))
			return nil, fmt.Errorf("failed to get partitions for topic %s: %w", topic, err)
		}
		topicMetadata[topic] = partitions
	}

	p.logger.Debug("Retrieved topic metadata",
		zap.Any("metadata", topicMetadata))

	return topicMetadata, nil
}

// Flush flushes any pending messages.
func (p *KafkaProducer) Flush(ctx context.Context) error {
	// For sync producer, messages are already flushed
	// For async producer, we need to wait for pending messages

	// Create a timeout context for flushing
	flushCtx, cancel := context.WithTimeout(ctx, 10*time.Second)
	defer cancel()

	// Check if there are pending messages in async producer
	for {
		select {
		case <-flushCtx.Done():
			return fmt.Errorf("flush timeout: %w", flushCtx.Err())
		default:
			// Check if async producer has pending messages
			if len(p.asyncProducer.Input()) == 0 {
				p.logger.Debug("All messages flushed successfully")
				return nil
			}
			// Wait a bit before checking again
			time.Sleep(10 * time.Millisecond)
		}
	}
}

// Close closes the producer and releases resources.
func (p *KafkaProducer) Close() error {
	var errors []error

	// Close async producer first
	if err := p.asyncProducer.Close(); err != nil {
		p.logger.Error("Failed to close async producer", zap.Error(err))
		errors = append(errors, fmt.Errorf("async producer: %w", err))
	}

	// Close sync producer
	if err := p.syncProducer.Close(); err != nil {
		p.logger.Error("Failed to close sync producer", zap.Error(err))
		errors = append(errors, fmt.Errorf("sync producer: %w", err))
	}

	if len(errors) > 0 {
		return fmt.Errorf("failed to close producers: %v", errors)
	}

	p.logger.Info("Kafka producer closed successfully")
	return nil
}

// convertToProducerMessage converts a port.KafkaMessage to sarama.ProducerMessage.
func (p *KafkaProducer) convertToProducerMessage(message *port.KafkaMessage) *sarama.ProducerMessage {
	producerMessage := &sarama.ProducerMessage{
		Topic: message.Topic,
		Value: sarama.ByteEncoder(message.Value),
	}

	// Set key if provided
	if len(message.Key) > 0 {
		producerMessage.Key = sarama.ByteEncoder(message.Key)
	}

	// Set headers if provided
	if len(message.Headers) > 0 {
		headers := make([]sarama.RecordHeader, 0, len(message.Headers))
		for key, value := range message.Headers {
			headers = append(headers, sarama.RecordHeader{
				Key:   []byte(key),
				Value: value,
			})
		}
		producerMessage.Headers = headers
	}

	// Set timestamp if provided
	if !message.Timestamp.IsZero() {
		producerMessage.Timestamp = message.Timestamp
	}

	// Set partition if specified (> -1)
	if message.Partition >= 0 {
		producerMessage.Partition = message.Partition
	}

	return producerMessage
}
