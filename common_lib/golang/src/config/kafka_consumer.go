package config

import "time"

type ConsumerConfig struct {
	Brokers []string

	GroupID string

	// Options: "earliest", "latest"
	AutoOffsetReset string

	EnableAutoCommit bool

	AutoCommitInterval time.Duration

	SessionTimeout time.Duration

	HeartbeatInterval time.Duration

	// Options: "roundrobin", "range", "sticky"
	RebalanceStrategy string

	FetchMinBytes int32

	FetchMaxBytes int32

	FetchMaxWait time.Duration
}
