package kafka

import (
	"time"

	"github.com/IBM/sarama"
)

type KafkaConfig struct {
	Brokers        []string
	RequiredAcks   sarama.RequiredAcks
	MaxRetries     int
	FlushFrequency time.Duration
	EnableSASL     bool
	SASLMechanism  string
	SASLUser       string
	SASLPassword   string
	EnableTLS      bool
}

func DefaultKafkaConfig() *KafkaConfig {
	return &KafkaConfig{
		Brokers:        []string{"localhost:9094"},
		RequiredAcks:   sarama.WaitForAll,
		MaxRetries:     3,
		FlushFrequency: 500 * time.Millisecond,
		EnableSASL:     false,
		EnableTLS:      false,
	}
}

func (c *KafkaConfig) ToSaramaConfig() *sarama.Config {
	config := sarama.NewConfig()

	// Producer settings
	config.Producer.RequiredAcks = c.RequiredAcks
	config.Producer.Retry.Max = c.MaxRetries
	config.Producer.Return.Successes = true
	config.Producer.Return.Errors = true
	config.Producer.Partitioner = sarama.NewRandomPartitioner
	config.Producer.Flush.Frequency = c.FlushFrequency

	// SASL configuration
	if c.EnableSASL {
		config.Net.SASL.Enable = true
		config.Net.SASL.User = c.SASLUser
		config.Net.SASL.Password = c.SASLPassword

		switch c.SASLMechanism {
		case "SCRAM-SHA-256":
			config.Net.SASL.Mechanism = sarama.SASLTypeSCRAMSHA256
		case "SCRAM-SHA-512":
			config.Net.SASL.Mechanism = sarama.SASLTypeSCRAMSHA512
		case "PLAIN":
			config.Net.SASL.Mechanism = sarama.SASLTypePlaintext
		default:
			config.Net.SASL.Mechanism = sarama.SASLTypePlaintext
		}
	}

	// TLS configuration
	if c.EnableTLS {
		config.Net.TLS.Enable = true
	}

	return config
}
