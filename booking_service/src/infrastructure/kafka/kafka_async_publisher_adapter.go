package kafka

import (
	"booking_service/core/port"
	"encoding/json"
	"fmt"
	"sync"
	"time"

	"github.com/IBM/sarama"
	"github.com/golibs-starter/golib/log"
)

type KafkaAsyncPublisherAdapter struct {
	producer sarama.AsyncProducer
	wg       sync.WaitGroup
}

func (k *KafkaAsyncPublisherAdapter) PushMessage(payload any, topic string) error {
	messageBytes, err := json.Marshal(payload)
	if err != nil {
		log.Error(nil, "Failed to marshal payload to JSON", err)
		return fmt.Errorf("failed to marshal payload: %w", err)
	}

	msg := &sarama.ProducerMessage{
		Topic:     topic,
		Value:     sarama.ByteEncoder(messageBytes),
		Timestamp: time.Now(),
	}

	select {
	case k.producer.Input() <- msg:
		log.Info(nil, fmt.Sprintf("Message queued for topic %s", topic))
		return nil
	case <-time.After(5 * time.Second):
		return fmt.Errorf("timeout sending message to topic %s", topic)
	}
}

func (k *KafkaAsyncPublisherAdapter) Close() error {
	k.producer.AsyncClose()
	k.wg.Wait()
	return nil
}

func (k *KafkaAsyncPublisherAdapter) handleSuccesses() {
	defer k.wg.Done()
	for success := range k.producer.Successes() {
		log.Info(nil, fmt.Sprintf("Message sent successfully to topic %s, partition %d, offset %d",
			success.Topic, success.Partition, success.Offset))
	}
}

func (k *KafkaAsyncPublisherAdapter) handleErrors() {
	defer k.wg.Done()
	for err := range k.producer.Errors() {
		log.Error(nil, fmt.Sprintf("Failed to send message to topic %s: %v", err.Msg.Topic, err.Err), err.Err)
	}
}

func NewKafkaAsyncPublisherAdapter() port.IKafkaPublisher {
	kafkaConfig := DefaultKafkaConfig()
	config := kafkaConfig.ToSaramaConfig()

	producer, err := sarama.NewAsyncProducer(kafkaConfig.Brokers, config)
	if err != nil {
		log.Error(nil, "Failed to create async Kafka producer", err)
		panic(fmt.Sprintf("Failed to create async Kafka producer: %v", err))
	}

	adapter := &KafkaAsyncPublisherAdapter{
		producer: producer,
	}

	// Start goroutines to handle successes and errors
	adapter.wg.Add(2)
	go adapter.handleSuccesses()
	go adapter.handleErrors()

	return adapter
}
