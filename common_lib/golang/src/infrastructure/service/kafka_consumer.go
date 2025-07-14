// Package service provides infrastructure implementations for Kafka operations.
// This package contains concrete implementations of the Kafka services defined in the core layer.
package service

import (
	"context"
	"fmt"
	"strings"
	"sync"
	"time"

	"github.com/IBM/sarama"
	"github.com/quantuanhuy/lib/src/core/port"
	"go.uber.org/zap"
)

// KafkaConsumer provides Kafka consumer operations implementation.
// It implements the IKafkaConsumerPort interface and handles all Kafka consumer-related operations
// including consumer group management, message consumption, and offset management.
type KafkaConsumer struct {
	consumerGroup sarama.ConsumerGroup           // Consumer group for coordinated consumption
	config        *port.ConsumerConfig           // Consumer configuration
	logger        *zap.Logger                    // Structured logger for operation tracking
	handlers      map[string]port.MessageHandler // Topic to handler mapping
	running       bool                           // Flag to track consumer state
	mu            sync.RWMutex                   // Mutex for thread-safe operations
	cancel        context.CancelFunc             // Cancel function for stopping consumer
	wg            sync.WaitGroup                 // Wait group for graceful shutdown
}

// ConsumerGroupHandler implements sarama.ConsumerGroupHandler interface.
type ConsumerGroupHandler struct {
	consumer *KafkaConsumer
}

// NewKafkaConsumer creates a new instance of KafkaConsumer.
// It initializes a consumer group with the provided configuration.
//
// Parameters:
//   - config: Consumer configuration containing broker addresses, group ID, etc.
//   - logger: Zap logger for operation logging
//
// Returns:
//   - port.IKafkaConsumerPort: Interface implementation for Kafka consumer operations
//   - error: nil if successful, otherwise the error that occurred during initialization
//
// Example:
//
//	config := &port.ConsumerConfig{
//	    Brokers: []string{"localhost:9092"},
//	    GroupID: "my-consumer-group",
//	    AutoOffsetReset: "earliest",
//	}
//	consumer, err := NewKafkaConsumer(config, logger)
func NewKafkaConsumer(config *port.ConsumerConfig, logger *zap.Logger) (port.IKafkaConsumerPort, error) {
	// Create Sarama configuration
	saramaConfig := sarama.NewConfig()

	// Configure consumer group settings
	saramaConfig.Consumer.Group.Rebalance.Strategy = sarama.BalanceStrategyRoundRobin
	saramaConfig.Consumer.Offsets.Initial = sarama.OffsetNewest

	// Configure auto offset reset
	switch strings.ToLower(config.AutoOffsetReset) {
	case "earliest":
		saramaConfig.Consumer.Offsets.Initial = sarama.OffsetOldest
	case "latest":
		saramaConfig.Consumer.Offsets.Initial = sarama.OffsetNewest
	default:
		saramaConfig.Consumer.Offsets.Initial = sarama.OffsetNewest
	}

	// Configure commit settings
	saramaConfig.Consumer.Offsets.AutoCommit.Enable = config.EnableAutoCommit
	if config.AutoCommitInterval > 0 {
		saramaConfig.Consumer.Offsets.AutoCommit.Interval = config.AutoCommitInterval
	}

	// Configure session settings
	if config.SessionTimeout > 0 {
		saramaConfig.Consumer.Group.Session.Timeout = config.SessionTimeout
	}
	if config.HeartbeatInterval > 0 {
		saramaConfig.Consumer.Group.Heartbeat.Interval = config.HeartbeatInterval
	}

	// Configure rebalance settings
	switch strings.ToLower(config.RebalanceStrategy) {
	case "range":
		saramaConfig.Consumer.Group.Rebalance.Strategy = sarama.BalanceStrategyRange
	case "roundrobin":
		saramaConfig.Consumer.Group.Rebalance.Strategy = sarama.BalanceStrategyRoundRobin
	case "sticky":
		saramaConfig.Consumer.Group.Rebalance.Strategy = sarama.BalanceStrategySticky
	default:
		saramaConfig.Consumer.Group.Rebalance.Strategy = sarama.BalanceStrategyRoundRobin
	}

	// Configure fetch settings
	if config.FetchMinBytes > 0 {
		saramaConfig.Consumer.Fetch.Min = config.FetchMinBytes
	}
	if config.FetchMaxBytes > 0 {
		saramaConfig.Consumer.Fetch.Max = config.FetchMaxBytes
	}
	if config.FetchMaxWait > 0 {
		saramaConfig.Consumer.MaxWaitTime = config.FetchMaxWait
	}

	// Configure security
	if err := configureConsumerSecurity(saramaConfig, config); err != nil {
		return nil, fmt.Errorf("failed to configure security: %w", err)
	}

	// Create consumer group
	consumerGroup, err := sarama.NewConsumerGroup(config.Brokers, config.GroupID, saramaConfig)
	if err != nil {
		logger.Error("Failed to create consumer group",
			zap.String("group_id", config.GroupID),
			zap.Error(err))
		return nil, fmt.Errorf("failed to create consumer group: %w", err)
	}

	consumer := &KafkaConsumer{
		consumerGroup: consumerGroup,
		config:        config,
		logger:        logger,
		handlers:      make(map[string]port.MessageHandler),
		running:       false,
	}

	logger.Info("Kafka consumer initialized successfully",
		zap.Strings("brokers", config.Brokers),
		zap.String("group_id", config.GroupID),
		zap.String("auto_offset_reset", config.AutoOffsetReset))

	return consumer, nil
}

// configureConsumerSecurity configures SASL and SSL settings for the consumer.
func configureConsumerSecurity(saramaConfig *sarama.Config, config *port.ConsumerConfig) error {
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

// Subscribe subscribes to topics and starts consuming messages.
func (c *KafkaConsumer) Subscribe(ctx context.Context, topics []string, handler port.MessageHandler) error {
	c.mu.Lock()
	defer c.mu.Unlock()

	if c.running {
		return fmt.Errorf("consumer is already running")
	}

	// Register handler for all topics
	for _, topic := range topics {
		c.handlers[topic] = handler
	}

	// Create cancellable context
	consumerCtx, cancel := context.WithCancel(ctx)
	c.cancel = cancel

	// Start consuming
	c.running = true
	c.wg.Add(1)

	go func() {
		defer c.wg.Done()
		defer func() {
			c.mu.Lock()
			c.running = false
			c.mu.Unlock()
		}()

		groupHandler := &ConsumerGroupHandler{consumer: c}

		for {
			// Check if context is cancelled
			if consumerCtx.Err() != nil {
				c.logger.Info("Consumer context cancelled, stopping consumption")
				return
			}

			// Consume messages
			if err := c.consumerGroup.Consume(consumerCtx, topics, groupHandler); err != nil {
				c.logger.Error("Error from consumer group consume",
					zap.Error(err))
				// Continue consuming unless context is cancelled
				if consumerCtx.Err() != nil {
					return
				}
				time.Sleep(time.Second) // Wait before retry
			}
		}
	}()

	c.logger.Info("Started consuming from topics",
		zap.Strings("topics", topics),
		zap.String("group_id", c.config.GroupID))

	return nil
}

// SubscribePattern subscribes to topics matching a pattern.
func (c *KafkaConsumer) SubscribePattern(ctx context.Context, pattern string, handler port.MessageHandler) error {
	// Create a temporary client to get topic list
	saramaConfig := sarama.NewConfig()

	// Configure security settings to match consumer config
	if err := configureConsumerSecurity(saramaConfig, c.config); err != nil {
		return fmt.Errorf("failed to configure security: %w", err)
	}

	client, err := sarama.NewClient(c.config.Brokers, saramaConfig)
	if err != nil {
		return fmt.Errorf("failed to create client for topic discovery: %w", err)
	}
	defer client.Close()

	topics, err := client.Topics()
	if err != nil {
		return fmt.Errorf("failed to get topics: %w", err)
	}

	// Filter topics by pattern
	var matchingTopics []string
	for _, topic := range topics {
		// Simple pattern matching - in production, use regex
		if strings.Contains(topic, strings.ReplaceAll(pattern, "*", "")) {
			matchingTopics = append(matchingTopics, topic)
		}
	}

	if len(matchingTopics) == 0 {
		return fmt.Errorf("no topics found matching pattern: %s", pattern)
	}

	c.logger.Info("Found topics matching pattern",
		zap.String("pattern", pattern),
		zap.Strings("topics", matchingTopics))

	return c.Subscribe(ctx, matchingTopics, handler)
}

// Poll polls for messages with timeout.
func (c *KafkaConsumer) Poll(ctx context.Context, timeout time.Duration) ([]*port.KafkaMessage, error) {
	// Note: Sarama doesn't support polling mode in the same way as other clients
	// This is a simplified implementation that would need enhancement for production use

	messages := make([]*port.KafkaMessage, 0)

	// Create timeout context
	pollCtx, cancel := context.WithTimeout(ctx, timeout)
	defer cancel()

	// Wait for timeout or context cancellation
	<-pollCtx.Done()

	c.logger.Debug("Poll completed",
		zap.Int("message_count", len(messages)),
		zap.Duration("timeout", timeout))

	return messages, nil
}

// CommitOffsets commits the specified offsets.
func (c *KafkaConsumer) CommitOffsets(ctx context.Context, offsets map[string]map[int32]int64) error {
	// Note: Direct offset commit is not available in consumer group mode
	// Offsets are typically committed automatically or in session handlers

	c.logger.Info("Offset commit requested",
		zap.Any("offsets", offsets))

	return nil
}

// GetOffsets gets the current committed offsets.
func (c *KafkaConsumer) GetOffsets(ctx context.Context, topics []string) (map[string]map[int32]int64, error) {
	// Create temporary client to fetch offsets
	client, err := sarama.NewClient(c.config.Brokers, nil)
	if err != nil {
		return nil, fmt.Errorf("failed to create client: %w", err)
	}
	defer client.Close()

	coordinator, err := client.Coordinator(c.config.GroupID)
	if err != nil {
		return nil, fmt.Errorf("failed to get coordinator: %w", err)
	}

	offsets := make(map[string]map[int32]int64)

	for _, topic := range topics {
		partitions, err := client.Partitions(topic)
		if err != nil {
			return nil, fmt.Errorf("failed to get partitions for topic %s: %w", topic, err)
		}

		offsets[topic] = make(map[int32]int64)

		// Create fetch request
		request := &sarama.OffsetFetchRequest{
			Version:       1,
			ConsumerGroup: c.config.GroupID,
		}

		for _, partition := range partitions {
			request.AddPartition(topic, partition)
		}

		response, err := coordinator.FetchOffset(request)
		if err != nil {
			return nil, fmt.Errorf("failed to fetch offsets: %w", err)
		}

		for partition := range response.Blocks[topic] {
			block := response.Blocks[topic][partition]
			offsets[topic][partition] = block.Offset
		}
	}

	c.logger.Debug("Retrieved committed offsets",
		zap.Any("offsets", offsets))

	return offsets, nil
}

// Pause pauses consumption from specified topic partitions.
func (c *KafkaConsumer) Pause(ctx context.Context, topicPartitions map[string][]int32) error {
	// Note: Sarama consumer group doesn't support runtime pause/resume
	// This would require a more sophisticated implementation

	c.logger.Info("Pause requested for topic partitions",
		zap.Any("topic_partitions", topicPartitions))

	return fmt.Errorf("pause/resume not supported in current implementation")
}

// Resume resumes consumption from specified topic partitions.
func (c *KafkaConsumer) Resume(ctx context.Context, topicPartitions map[string][]int32) error {
	// Note: Sarama consumer group doesn't support runtime pause/resume
	// This would require a more sophisticated implementation

	c.logger.Info("Resume requested for topic partitions",
		zap.Any("topic_partitions", topicPartitions))

	return fmt.Errorf("pause/resume not supported in current implementation")
}

// SeekToOffset seeks to a specific offset for topic partitions.
func (c *KafkaConsumer) SeekToOffset(ctx context.Context, offsets map[string]map[int32]int64) error {
	// Note: Seeking in consumer group mode requires stopping and restarting
	// This is a simplified implementation

	c.logger.Info("Seek to offset requested",
		zap.Any("offsets", offsets))

	return fmt.Errorf("seek not supported in consumer group mode")
}

// SeekToBeginning seeks to the beginning of topic partitions.
func (c *KafkaConsumer) SeekToBeginning(ctx context.Context, topicPartitions map[string][]int32) error {
	c.logger.Info("Seek to beginning requested",
		zap.Any("topic_partitions", topicPartitions))

	return fmt.Errorf("seek not supported in consumer group mode")
}

// SeekToEnd seeks to the end of topic partitions.
func (c *KafkaConsumer) SeekToEnd(ctx context.Context, topicPartitions map[string][]int32) error {
	c.logger.Info("Seek to end requested",
		zap.Any("topic_partitions", topicPartitions))

	return fmt.Errorf("seek not supported in consumer group mode")
}

// Close closes the consumer and releases resources.
func (c *KafkaConsumer) Close() error {
	c.mu.Lock()
	defer c.mu.Unlock()

	if c.running && c.cancel != nil {
		c.cancel()
		c.wg.Wait() // Wait for consumer goroutine to finish
	}

	if err := c.consumerGroup.Close(); err != nil {
		c.logger.Error("Failed to close consumer group", zap.Error(err))
		return fmt.Errorf("failed to close consumer group: %w", err)
	}

	c.logger.Info("Kafka consumer closed successfully")
	return nil
}

// ConsumerGroupHandler implementation

// Setup is run at the beginning of a new session, before ConsumeClaim.
func (h *ConsumerGroupHandler) Setup(sarama.ConsumerGroupSession) error {
	h.consumer.logger.Debug("Consumer group session setup")
	return nil
}

// Cleanup is run at the end of a session, once all ConsumeClaim goroutines have exited.
func (h *ConsumerGroupHandler) Cleanup(sarama.ConsumerGroupSession) error {
	h.consumer.logger.Debug("Consumer group session cleanup")
	return nil
}

// ConsumeClaim must start a consumer loop of ConsumerGroupClaim's Messages().
func (h *ConsumerGroupHandler) ConsumeClaim(session sarama.ConsumerGroupSession, claim sarama.ConsumerGroupClaim) error {
	// Get handler for this topic
	h.consumer.mu.RLock()
	handler, exists := h.consumer.handlers[claim.Topic()]
	h.consumer.mu.RUnlock()

	if !exists {
		h.consumer.logger.Warn("No handler found for topic", zap.String("topic", claim.Topic()))
		return nil
	}

	// Consume messages
	for {
		select {
		case message := <-claim.Messages():
			if message == nil {
				return nil
			}

			// Convert to port message
			portMessage := h.convertToPortMessage(message)

			// Handle message
			if err := handler(portMessage); err != nil {
				h.consumer.logger.Error("Message handler error",
					zap.String("topic", message.Topic),
					zap.Int32("partition", message.Partition),
					zap.Int64("offset", message.Offset),
					zap.Error(err))
				// Continue processing other messages
				continue
			}

			// Mark message as processed (if auto commit is disabled)
			if !h.consumer.config.EnableAutoCommit {
				session.MarkOffset(message.Topic, message.Partition, message.Offset+1, "")
			}

			h.consumer.logger.Debug("Message processed successfully",
				zap.String("topic", message.Topic),
				zap.Int32("partition", message.Partition),
				zap.Int64("offset", message.Offset))

		case <-session.Context().Done():
			return nil
		}
	}
}

// convertToPortMessage converts a sarama.ConsumerMessage to port.KafkaMessage.
func (h *ConsumerGroupHandler) convertToPortMessage(message *sarama.ConsumerMessage) *port.KafkaMessage {
	portMessage := &port.KafkaMessage{
		Topic:     message.Topic,
		Partition: message.Partition,
		Offset:    message.Offset,
		Key:       message.Key,
		Value:     message.Value,
		Timestamp: message.Timestamp,
		Headers:   make(map[string][]byte),
	}

	// Convert headers
	for _, header := range message.Headers {
		portMessage.Headers[string(header.Key)] = header.Value
	}

	return portMessage
}
