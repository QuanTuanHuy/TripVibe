package kafka

// Configuration defines settings for Kafka connectivity
type Configuration struct {
	Brokers              []string `mapstructure:"brokers"`
	ConsumerGroupID      string   `mapstructure:"consumer-group-id"`
	MaxRetryCount        int      `mapstructure:"max-retry-count"`
	EmailProducerEnabled bool     `mapstructure:"email-producer-enabled"`
	EmailConsumerEnabled bool     `mapstructure:"email-consumer-enabled"`
}

// NewConfiguration creates a default configuration
func NewConfiguration() *Configuration {
	return &Configuration{
		Brokers:              []string{"localhost:9092"},
		ConsumerGroupID:      "notification-service",
		MaxRetryCount:        3,
		EmailProducerEnabled: true,
		EmailConsumerEnabled: true,
	}
}
