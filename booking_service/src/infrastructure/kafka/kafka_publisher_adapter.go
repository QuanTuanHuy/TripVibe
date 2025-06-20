package kafka

import (
	"booking_service/core/port"
	"encoding/json"
	"fmt"
	"time"

	"github.com/IBM/sarama"
	"github.com/golibs-starter/golib/log"
)

type KafkaPublisherAdapter struct {
	producer sarama.SyncProducer
}

func (k *KafkaPublisherAdapter) PushMessage(payload any, topic string) error {
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

	partition, offset, err := k.producer.SendMessage(msg)
	if err != nil {
		log.Error(nil, "Failed to send message to Kafka", err)
		return fmt.Errorf("failed to send message: %w", err)
	}

	log.Info(nil, fmt.Sprintf("Message sent successfully to topic %s, partition %d, offset %d", topic, partition, offset))
	return nil
}

func (k *KafkaPublisherAdapter) Close() error {
	if k.producer != nil {
		return k.producer.Close()
	}
	return nil
}

func NewKafkaPublisherAdapter() port.IKafkaPublisher {
	kafkaConfig := DefaultKafkaConfig()
	config := kafkaConfig.ToSaramaConfig()

	producer, err := sarama.NewSyncProducer(kafkaConfig.Brokers, config)
	if err != nil {
		log.Error(nil, "Failed to create Kafka producer", err)
		panic(fmt.Sprintf("Failed to create Kafka producer: %v", err))
	}

	return &KafkaPublisherAdapter{
		producer: producer,
	}
}
