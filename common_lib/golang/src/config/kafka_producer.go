package config

import "time"

type ProducerConfig struct {
	Brokers []string

	RequiredAcks int16

	MaxMessageBytes int

	CompressionType string

	RetryMax int

	RetryBackoff time.Duration

	FlushFrequency time.Duration

	FlushMessages int

	FlushBytes int

	EnableIdempotence bool

	TransactionID string
}
