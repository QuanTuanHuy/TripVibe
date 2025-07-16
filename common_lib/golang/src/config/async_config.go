package config

import (
	"time"
)

type AsyncConfig struct {
	// Kafka Topics
	RequestTopic string `json:"request_topic" yaml:"request_topic"`
	ReplyTopic   string `json:"reply_topic" yaml:"reply_topic"`

	// Timeouts
	DefaultTimeout time.Duration `json:"default_timeout" yaml:"default_timeout"`
	RequestTimeout time.Duration `json:"request_timeout" yaml:"request_timeout"`
	ReplyTimeout   time.Duration `json:"reply_timeout" yaml:"reply_timeout"`

	// Concurrency
	MaxConcurrency     int `json:"max_concurrency" yaml:"max_concurrency"`
	MaxRequestHandlers int `json:"max_request_handlers" yaml:"max_request_handlers"`
	MaxReplyHandlers   int `json:"max_reply_handlers" yaml:"max_reply_handlers"`

	// Cleanup
	CleanupInterval     time.Duration `json:"cleanup_interval" yaml:"cleanup_interval"`
	CorrelationTTL      time.Duration `json:"correlation_ttl" yaml:"correlation_ttl"`
	ExpiredCleanupBatch int           `json:"expired_cleanup_batch" yaml:"expired_cleanup_batch"`

	// Retry
	RetryAttempts    int           `json:"retry_attempts" yaml:"retry_attempts"`
	RetryDelay       time.Duration `json:"retry_delay" yaml:"retry_delay"`
	RetryBackoffRate float64       `json:"retry_backoff_rate" yaml:"retry_backoff_rate"`

	// Monitoring
	MetricsEnabled     bool          `json:"metrics_enabled" yaml:"metrics_enabled"`
	MonitoringInterval time.Duration `json:"monitoring_interval" yaml:"monitoring_interval"`
}

func DefaultAsyncConfig() *AsyncConfig {
	return &AsyncConfig{
		RequestTopic: "async-requests",
		ReplyTopic:   "async-replies",

		DefaultTimeout: 30 * time.Second,
		RequestTimeout: 60 * time.Second,
		ReplyTimeout:   30 * time.Second,

		MaxConcurrency:     100,
		MaxRequestHandlers: 10,
		MaxReplyHandlers:   10,

		CleanupInterval:     5 * time.Minute,
		CorrelationTTL:      1 * time.Hour,
		ExpiredCleanupBatch: 100,

		RetryAttempts:    3,
		RetryDelay:       time.Second,
		RetryBackoffRate: 2.0,

		MetricsEnabled:     true,
		MonitoringInterval: 30 * time.Second,
	}
}
