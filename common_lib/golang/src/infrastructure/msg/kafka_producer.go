package msg

import (
	"context"
	"fmt"
	"strings"
	"sync"
	"time"

	"github.com/IBM/sarama"
	"github.com/quantuanhuy/lib/src/config"
	entity "github.com/quantuanhuy/lib/src/core/entity/msg"
	"github.com/quantuanhuy/lib/src/core/port"
	"go.uber.org/zap"
)

type callbackInfo struct {
	message         *entity.KafkaMessage
	successCallback func(*entity.KafkaMessage)
	errorCallback   func(*entity.KafkaMessage, error)
}

type KafkaProducer struct {
	syncProducer     sarama.SyncProducer
	asyncProducer    sarama.AsyncProducer
	config           *config.ProducerConfig
	logger           *zap.Logger
	inTransaction    bool
	callbackMap      map[string]*callbackInfo
	callbackMapMutex sync.RWMutex
}

func (k *KafkaProducer) handleAsyncProducerMessages() {
	for {
		select {
		case success := <-k.asyncProducer.Successes():
			k.logger.Info("Async message sent successfully",
				zap.String("topic", success.Topic),
				zap.Int32("partition", success.Partition),
				zap.Int64("offset", success.Offset))

			// Handle success callback
			k.handleSuccessCallback(success)

		case err := <-k.asyncProducer.Errors():
			k.logger.Error("Failed to send async message",
				zap.Error(err.Err),
				zap.String("topic", err.Msg.Topic))

			// Handle error callback
			k.handleErrorCallback(err)
		}
	}
}

func (k *KafkaProducer) handleSuccessCallback(msg *sarama.ProducerMessage) {
	if msg.Metadata == nil {
		return
	}

	messageID, ok := msg.Metadata.(string)
	if !ok {
		return
	}

	k.callbackMapMutex.Lock()
	callbackInfo, exists := k.callbackMap[messageID]
	if exists {
		delete(k.callbackMap, messageID)
	}
	k.callbackMapMutex.Unlock()

	if exists && callbackInfo.successCallback != nil {
		// Update message with actual values
		callbackInfo.message.Partition = &msg.Partition
		callbackInfo.message.Offset = msg.Offset

		callbackInfo.successCallback(callbackInfo.message)
	}
}

func (k *KafkaProducer) handleErrorCallback(err *sarama.ProducerError) {
	if err.Msg.Metadata == nil {
		return
	}

	messageID, ok := err.Msg.Metadata.(string)
	if !ok {
		return
	}

	k.callbackMapMutex.Lock()
	callbackInfo, exists := k.callbackMap[messageID]
	if exists {
		delete(k.callbackMap, messageID)
	}
	k.callbackMapMutex.Unlock()

	if exists && callbackInfo.errorCallback != nil {
		callbackInfo.errorCallback(callbackInfo.message, err.Err)
	}
}

func (k *KafkaProducer) generateMessageID() string {
	return fmt.Sprintf("%d", time.Now().UnixNano())
}

func (k *KafkaProducer) SendMessage(ctx context.Context, message *entity.KafkaMessage) error {
	saramaMessage := k.ConvertToSaramaMessage(message)
	partition, offset, err := k.syncProducer.SendMessage(saramaMessage)

	if err != nil {
		k.logger.Error("Failed to send message",
			zap.Error(err),
			zap.String("topic", message.Topic))
		return fmt.Errorf("failed to send message: %w", err)
	}

	k.logger.Info("Message sent successfully",
		zap.String("topic", message.Topic),
		zap.Int32("partition", partition),
		zap.Int64("offset", offset))

	message.Partition = &partition
	message.Offset = offset

	return nil
}

func (k *KafkaProducer) SendMessageAsync(ctx context.Context, message *entity.KafkaMessage, successCallback func(*entity.KafkaMessage), errorCallBack func(*entity.KafkaMessage, error)) error {
	saramaMessage := k.ConvertToSaramaMessage(message)

	// Generate unique message ID and store callback info
	messageID := k.generateMessageID()
	saramaMessage.Metadata = messageID

	// Store callback info if callbacks are provided
	if successCallback != nil || errorCallBack != nil {
		k.callbackMapMutex.Lock()
		k.callbackMap[messageID] = &callbackInfo{
			message:         message,
			successCallback: successCallback,
			errorCallback:   errorCallBack,
		}
		k.callbackMapMutex.Unlock()
	}

	select {
	case k.asyncProducer.Input() <- saramaMessage:
		k.logger.Info("Message sent to async producer",
			zap.String("topic", message.Topic),
			zap.String("message_id", messageID))
		return nil
	case <-ctx.Done():
		// Clean up callback info on context cancellation
		if successCallback != nil || errorCallBack != nil {
			k.callbackMapMutex.Lock()
			delete(k.callbackMap, messageID)
			k.callbackMapMutex.Unlock()
		}
		return ctx.Err()
	default:
		// Clean up callback info on failure
		if successCallback != nil || errorCallBack != nil {
			k.callbackMapMutex.Lock()
			delete(k.callbackMap, messageID)
			k.callbackMapMutex.Unlock()
		}
		return fmt.Errorf("failed to send message to async producer: %s, input channel is full", message.Topic)
	}
}

func (k *KafkaProducer) SendMessages(ctx context.Context, messages []*entity.KafkaMessage) error {
	if len(messages) == 0 {
		return nil
	}

	var errors []error
	for i, message := range messages {
		if err := k.SendMessage(ctx, message); err != nil {
			k.logger.Error("Failed to send message",
				zap.Error(err),
				zap.String("topic", message.Topic),
				zap.Int("msg_index", i))
			errors = append(errors, err)
		}
	}

	if len(errors) > 0 {
		return fmt.Errorf("failed to send %d of %d messages: %v", len(errors), len(messages), errors)
	}

	k.logger.Info("All messages sent successfully",
		zap.Int("total_messages", len(messages)))

	return nil
}

func (k *KafkaProducer) SendMessagesAsync(ctx context.Context, messages []*entity.KafkaMessage, successCallback func(*entity.KafkaMessage), errorCallBack func(*entity.KafkaMessage, error)) error {
	if len(messages) == 0 {
		return nil
	}

	var failedMessages []string

	for i, message := range messages {
		if err := k.SendMessageAsync(ctx, message, successCallback, errorCallBack); err != nil {
			k.logger.Error("Failed to send message in batch",
				zap.Error(err),
				zap.String("topic", message.Topic),
				zap.Int("msg_index", i))

			failedMessages = append(failedMessages, fmt.Sprintf("msg[%d]: %s", i, err.Error()))

			// Continue sending other messages instead of early return
			continue
		}
	}

	if len(failedMessages) > 0 {
		return fmt.Errorf("failed to send %d of %d messages: %v", len(failedMessages), len(messages), failedMessages)
	}

	k.logger.Info("All async messages sent to producer successfully",
		zap.Int("total_messages", len(messages)))

	return nil
}

func (k *KafkaProducer) BeginTransaction(ctx context.Context) error {
	if k.config.TransactionID == "" {
		return fmt.Errorf("transaction ID is not set in the producer config")
	}

	if k.inTransaction {
		return fmt.Errorf("transaction already in progress")
	}

	k.inTransaction = true

	k.logger.Info("Transaction started",
		zap.String("transaction_id", k.config.TransactionID))

	return nil
}

func (k *KafkaProducer) CommitTransaction(ctx context.Context) error {
	if !k.inTransaction {
		return fmt.Errorf("no transaction in progress to commit")
	}

	if err := k.Flush(ctx); err != nil {
		k.logger.Error("Failed to flush messages before committing transaction",
			zap.Error(err),
			zap.String("transaction_id", k.config.TransactionID))
		return err
	}

	k.logger.Info("Transaction committed",
		zap.String("transaction_id", k.config.TransactionID))

	return nil
}

func (k *KafkaProducer) AbortTransaction(ctx context.Context) error {
	if !k.inTransaction {
		return fmt.Errorf("no transaction in progress")
	}

	k.inTransaction = false

	k.logger.Info("Transaction aborted",
		zap.String("transaction_id", k.config.TransactionID))

	return nil
}

func (k *KafkaProducer) Close() error {
	var errors []error

	// Close async producer first
	if err := k.asyncProducer.Close(); err != nil {
		k.logger.Error("Failed to close async producer", zap.Error(err))
		errors = append(errors, fmt.Errorf("async producer: %w", err))
	}

	// Close sync producer
	if err := k.syncProducer.Close(); err != nil {
		k.logger.Error("Failed to close sync producer", zap.Error(err))
		errors = append(errors, fmt.Errorf("sync producer: %w", err))
	}

	// Clean up callback map
	k.callbackMapMutex.Lock()
	for messageID, callbackInfo := range k.callbackMap {
		if callbackInfo.errorCallback != nil {
			callbackInfo.errorCallback(callbackInfo.message, fmt.Errorf("producer closed before message was sent"))
		}
		delete(k.callbackMap, messageID)
	}
	k.callbackMapMutex.Unlock()

	if len(errors) > 0 {
		return fmt.Errorf("failed to close producers: %v", errors)
	}

	k.logger.Info("Kafka producer closed successfully")
	return nil
}

func (k *KafkaProducer) Flush(ctx context.Context) error {
	flushCtx, cancel := context.WithTimeout(ctx, 10*time.Second)
	defer cancel()

	// Check if there are pending messages in async producer
	for {
		select {
		case <-flushCtx.Done():
			return fmt.Errorf("flush timeout: %w", flushCtx.Err())
		default:
			// Check if async producer has pending messages
			if len(k.asyncProducer.Input()) == 0 {
				k.logger.Debug("All messages flushed successfully")
				return nil
			}
			// Wait a bit before checking again
			time.Sleep(10 * time.Millisecond)
		}
	}
}

func (k *KafkaProducer) SendMessageToPartition(ctx context.Context, message *entity.KafkaMessage, partition int32) error {
	// Update message partition
	originalPartition := message.Partition
	message.Partition = &partition

	saramaMessage := k.ConvertToSaramaMessage(message)
	saramaMessage.Partition = partition

	actualPartition, offset, err := k.syncProducer.SendMessage(saramaMessage)
	if err != nil {
		// Restore original partition on error
		message.Partition = originalPartition
		k.logger.Error("Failed to send message to specific partition",
			zap.String("topic", message.Topic),
			zap.Int32("target_partition", partition),
			zap.Error(err))
		return fmt.Errorf("failed to send message to partition %d: %w", partition, err)
	}

	k.logger.Info("Message sent to specific partition",
		zap.String("topic", message.Topic),
		zap.Int32("partition", actualPartition),
		zap.Int64("offset", offset))

	// Update message with actual values
	message.Partition = &actualPartition
	message.Offset = offset

	return nil
}

func (k *KafkaProducer) ConvertToSaramaMessage(message *entity.KafkaMessage) *sarama.ProducerMessage {
	saramaMessage := &sarama.ProducerMessage{
		Topic: message.Topic,
		Value: sarama.ByteEncoder(message.Value),
	}

	if len(message.Key) > 0 {
		saramaMessage.Key = sarama.ByteEncoder(message.Key)
	}

	if len(message.Headers) > 0 {
		headers := make([]sarama.RecordHeader, 0, len(message.Headers))
		for key, value := range message.Headers {
			headers = append(headers, sarama.RecordHeader{
				Key:   []byte(key),
				Value: value,
			})
		}
		saramaMessage.Headers = headers
	}

	if message.TimeStamp > 0 {
		saramaMessage.Timestamp = time.Unix(0, message.TimeStamp*int64(time.Millisecond))
	}

	if message.Partition != nil {
		saramaMessage.Partition = *message.Partition
	}

	return saramaMessage
}

func NewKafkaProducer(config *config.ProducerConfig, logger *zap.Logger) (port.IKafkaProducerPort, error) {
	saramaConfig := sarama.NewConfig()

	saramaConfig.Producer.Return.Successes = true
	saramaConfig.Producer.RequiredAcks = sarama.RequiredAcks(config.RequiredAcks)
	saramaConfig.Producer.Retry.Max = config.RetryMax
	saramaConfig.Producer.Retry.Backoff = config.RetryBackoff

	if config.MaxMessageBytes > 0 {
		saramaConfig.Producer.MaxMessageBytes = config.MaxMessageBytes
	}

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

	if config.FlushFrequency > 0 {
		saramaConfig.Producer.Flush.Frequency = config.FlushFrequency
	}
	if config.FlushMessages > 0 {
		saramaConfig.Producer.Flush.Messages = config.FlushMessages
	}
	if config.FlushBytes > 0 {
		saramaConfig.Producer.Flush.Bytes = config.FlushBytes
	}

	if config.EnableIdempotence {
		saramaConfig.Producer.Idempotent = true
		saramaConfig.Net.MaxOpenRequests = 1
	}
	if config.TransactionID != "" {
		saramaConfig.Producer.Transaction.ID = config.TransactionID
		saramaConfig.Producer.Idempotent = true
	}

	syncProducer, err := sarama.NewSyncProducer(config.Brokers, saramaConfig)
	if err != nil {
		logger.Error("Failed to create sync producer", zap.Error(err))
		return nil, fmt.Errorf("failed to create sync producer: %w", err)
	}

	asyncProducer, err := sarama.NewAsyncProducer(config.Brokers, saramaConfig)
	if err != nil {
		logger.Error("Failed to create async producer", zap.Error(err))
		return nil, fmt.Errorf("failed to create async producer: %w", err)
	}

	producer := &KafkaProducer{
		syncProducer:     syncProducer,
		asyncProducer:    asyncProducer,
		config:           config,
		logger:           logger,
		inTransaction:    false,
		callbackMap:      make(map[string]*callbackInfo),
		callbackMapMutex: sync.RWMutex{},
	}

	go producer.handleAsyncProducerMessages()

	return producer, nil
}
