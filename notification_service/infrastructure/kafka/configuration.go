package kafka

import "notification_service/kernel/properties"

type Configuration struct {
	Brokers              []string `mapstructure:"brokers"`
	ConsumerGroupID      string   `mapstructure:"consumer-group-id"`
	MaxRetryCount        int      `mapstructure:"max-retry-count"`
	EmailProducerEnabled bool     `mapstructure:"email-producer-enabled"`
	EmailConsumerEnabled bool     `mapstructure:"email-consumer-enabled"`
}

// NewConfiguration creates a default configuration
func NewConfiguration(kafkaProperty *properties.KafkaProperties, consumerProperty *properties.EmailConsumerProperties) *Configuration {
	return &Configuration{
		Brokers:              kafkaProperty.BootstrapServers,
		ConsumerGroupID:      consumerProperty.GroupId,
		MaxRetryCount:        3,
		EmailProducerEnabled: true,
		EmailConsumerEnabled: true,
	}
}
