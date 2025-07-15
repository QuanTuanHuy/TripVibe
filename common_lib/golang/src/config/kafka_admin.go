package config

import "time"

type KafkaAdminConfig struct {
	Brokers []string

	RequestTimeout time.Duration

	RetryMax int

	RetryBackoff time.Duration
}
