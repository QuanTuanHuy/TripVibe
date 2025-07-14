// Package service_test provides comprehensive test cases for Kafka services.
// This package tests producer, consumer, and admin functionality.
package service_test

import (
	"testing"
	"time"

	"github.com/quantuanhuy/lib/src/core/port"
	"github.com/quantuanhuy/lib/src/infrastructure/service"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/require"
	"go.uber.org/zap/zaptest"
)

// TestKafkaProducer_NewKafkaProducer tests the creation of a new Kafka producer.
func TestKafkaProducer_NewKafkaProducer(t *testing.T) {
	logger := zaptest.NewLogger(t)

	tests := []struct {
		name        string
		config      *port.ProducerConfig
		expectError bool
	}{
		{
			name:        "Valid configuration",
			config:      port.DefaultProducerConfig(),
			expectError: true, // Expected because no Kafka broker running
		},
		{
			name: "Invalid brokers",
			config: &port.ProducerConfig{
				Brokers:      []string{},
				RequiredAcks: 1,
			},
			expectError: true,
		},
		{
			name: "Valid configuration with compression",
			config: &port.ProducerConfig{
				Brokers:         []string{"localhost:9092"},
				RequiredAcks:    1,
				CompressionType: "gzip",
				RetryMax:        3,
				RetryBackoff:    100 * time.Millisecond,
			},
			expectError: true, // Expected because no Kafka broker running
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			producer, err := service.NewKafkaProducer(tt.config, logger)

			if tt.expectError {
				assert.Error(t, err)
				assert.Nil(t, producer)
			} else {
				assert.NoError(t, err)
				assert.NotNil(t, producer)
				if producer != nil {
					err := producer.Close()
					assert.NoError(t, err)
				}
			}
		})
	}
}

// TestKafkaProducer_ConfigValidation tests producer configuration validation.
func TestKafkaProducer_ConfigValidation(t *testing.T) {
	tests := []struct {
		name        string
		config      *port.ProducerConfig
		expectError bool
	}{
		{
			name:        "Valid config",
			config:      port.DefaultProducerConfig(),
			expectError: false,
		},
		{
			name: "Empty brokers",
			config: &port.ProducerConfig{
				Brokers: []string{},
			},
			expectError: true,
		},
		{
			name: "Invalid required acks",
			config: &port.ProducerConfig{
				Brokers:      []string{"localhost:9092"},
				RequiredAcks: 2, // Invalid value
			},
			expectError: true,
		},
		{
			name: "Invalid compression type",
			config: &port.ProducerConfig{
				Brokers:         []string{"localhost:9092"},
				RequiredAcks:    1,
				CompressionType: "invalid",
			},
			expectError: true,
		},
		{
			name: "Negative retry max",
			config: &port.ProducerConfig{
				Brokers:      []string{"localhost:9092"},
				RequiredAcks: 1,
				RetryMax:     -1,
			},
			expectError: true,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			err := tt.config.Validate()

			if tt.expectError {
				assert.Error(t, err)
			} else {
				assert.NoError(t, err)
			}
		})
	}
}

// TestKafkaConsumer_NewKafkaConsumer tests the creation of a new Kafka consumer.
func TestKafkaConsumer_NewKafkaConsumer(t *testing.T) {
	logger := zaptest.NewLogger(t)

	tests := []struct {
		name        string
		config      *port.ConsumerConfig
		expectError bool
	}{
		{
			name:        "Valid configuration",
			config:      port.DefaultConsumerConfig(),
			expectError: true, // Expected because no Kafka broker running
		},
		{
			name: "Invalid configuration - empty group ID",
			config: &port.ConsumerConfig{
				Brokers: []string{"localhost:9092"},
				GroupID: "",
			},
			expectError: true,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			consumer, err := service.NewKafkaConsumer(tt.config, logger)

			if tt.expectError {
				assert.Error(t, err)
				assert.Nil(t, consumer)
			} else {
				assert.NoError(t, err)
				assert.NotNil(t, consumer)
				if consumer != nil {
					err := consumer.Close()
					assert.NoError(t, err)
				}
			}
		})
	}
}

// TestKafkaConsumer_ConfigValidation tests consumer configuration validation.
func TestKafkaConsumer_ConfigValidation(t *testing.T) {
	tests := []struct {
		name        string
		config      *port.ConsumerConfig
		expectError bool
	}{
		{
			name:        "Valid config",
			config:      port.DefaultConsumerConfig(),
			expectError: false,
		},
		{
			name: "Empty brokers",
			config: &port.ConsumerConfig{
				Brokers: []string{},
				GroupID: "test-group",
			},
			expectError: true,
		},
		{
			name: "Empty group ID",
			config: &port.ConsumerConfig{
				Brokers: []string{"localhost:9092"},
				GroupID: "",
			},
			expectError: true,
		},
		{
			name: "Invalid auto offset reset",
			config: &port.ConsumerConfig{
				Brokers:         []string{"localhost:9092"},
				GroupID:         "test-group",
				AutoOffsetReset: "invalid",
			},
			expectError: true,
		},
		{
			name: "Invalid rebalance strategy",
			config: &port.ConsumerConfig{
				Brokers:           []string{"localhost:9092"},
				GroupID:           "test-group",
				RebalanceStrategy: "invalid",
			},
			expectError: true,
		},
		{
			name: "Negative fetch min bytes",
			config: &port.ConsumerConfig{
				Brokers:       []string{"localhost:9092"},
				GroupID:       "test-group",
				FetchMinBytes: -1,
			},
			expectError: true,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			err := tt.config.Validate()

			if tt.expectError {
				assert.Error(t, err)
			} else {
				assert.NoError(t, err)
			}
		})
	}
}

// TestKafkaAdmin_NewKafkaAdmin tests the creation of a new Kafka admin client.
func TestKafkaAdmin_NewKafkaAdmin(t *testing.T) {
	logger := zaptest.NewLogger(t)

	tests := []struct {
		name        string
		config      *port.AdminConfig
		expectError bool
	}{
		{
			name:        "Valid configuration",
			config:      port.DefaultAdminConfig(),
			expectError: true, // Expected because no Kafka broker running
		},
		{
			name: "Invalid brokers",
			config: &port.AdminConfig{
				Brokers: []string{},
			},
			expectError: true,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			admin, err := service.NewKafkaAdmin(tt.config, logger)

			if tt.expectError {
				assert.Error(t, err)
				assert.Nil(t, admin)
			} else {
				assert.NoError(t, err)
				assert.NotNil(t, admin)
				if admin != nil {
					err := admin.Close()
					assert.NoError(t, err)
				}
			}
		})
	}
}

// TestKafkaAdmin_ConfigValidation tests admin configuration validation.
func TestKafkaAdmin_ConfigValidation(t *testing.T) {
	tests := []struct {
		name        string
		config      *port.AdminConfig
		expectError bool
	}{
		{
			name:        "Valid config",
			config:      port.DefaultAdminConfig(),
			expectError: false,
		},
		{
			name: "Empty brokers",
			config: &port.AdminConfig{
				Brokers: []string{},
			},
			expectError: true,
		},
		{
			name: "Zero request timeout",
			config: &port.AdminConfig{
				Brokers:        []string{"localhost:9092"},
				RequestTimeout: 0,
			},
			expectError: true,
		},
		{
			name: "Negative retry max",
			config: &port.AdminConfig{
				Brokers:        []string{"localhost:9092"},
				RequestTimeout: 30 * time.Second,
				RetryMax:       -1,
			},
			expectError: true,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			err := tt.config.Validate()

			if tt.expectError {
				assert.Error(t, err)
			} else {
				assert.NoError(t, err)
			}
		})
	}
}

// TestTopicConfig_Validation tests topic configuration validation.
func TestTopicConfig_Validation(t *testing.T) {
	tests := []struct {
		name        string
		config      *port.TopicConfig
		expectError bool
	}{
		{
			name: "Valid config",
			config: &port.TopicConfig{
				Name:              "test-topic",
				NumPartitions:     3,
				ReplicationFactor: 1,
			},
			expectError: false,
		},
		{
			name: "Empty topic name",
			config: &port.TopicConfig{
				Name:              "",
				NumPartitions:     3,
				ReplicationFactor: 1,
			},
			expectError: true,
		},
		{
			name: "Zero partitions",
			config: &port.TopicConfig{
				Name:              "test-topic",
				NumPartitions:     0,
				ReplicationFactor: 1,
			},
			expectError: true,
		},
		{
			name: "Zero replication factor",
			config: &port.TopicConfig{
				Name:              "test-topic",
				NumPartitions:     3,
				ReplicationFactor: 0,
			},
			expectError: true,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			err := tt.config.Validate()

			if tt.expectError {
				assert.Error(t, err)
			} else {
				assert.NoError(t, err)
			}
		})
	}
}

// TestKafkaMessage_Structure tests the Kafka message structure.
func TestKafkaMessage_Structure(t *testing.T) {
	message := &port.KafkaMessage{
		Topic:     "test-topic",
		Key:       []byte("test-key"),
		Value:     []byte("test-value"),
		Headers:   map[string][]byte{"header1": []byte("value1")},
		Timestamp: time.Now(),
		Partition: 0,
		Offset:    123,
	}

	assert.Equal(t, "test-topic", message.Topic)
	assert.Equal(t, []byte("test-key"), message.Key)
	assert.Equal(t, []byte("test-value"), message.Value)
	assert.Equal(t, []byte("value1"), message.Headers["header1"])
	assert.Equal(t, int32(0), message.Partition)
	assert.Equal(t, int64(123), message.Offset)
	assert.False(t, message.Timestamp.IsZero())
}

// TestDefaultConfigurations tests that default configurations are valid.
func TestDefaultConfigurations(t *testing.T) {
	t.Run("Default Producer Config", func(t *testing.T) {
		config := port.DefaultProducerConfig()
		require.NotNil(t, config)

		err := config.Validate()
		assert.NoError(t, err)

		assert.NotEmpty(t, config.Brokers)
		assert.Equal(t, int16(1), config.RequiredAcks)
		assert.Equal(t, "none", config.CompressionType)
		assert.Equal(t, "PLAINTEXT", config.SecurityProtocol)
	})

	t.Run("Default Consumer Config", func(t *testing.T) {
		config := port.DefaultConsumerConfig()
		require.NotNil(t, config)

		err := config.Validate()
		assert.NoError(t, err)

		assert.NotEmpty(t, config.Brokers)
		assert.NotEmpty(t, config.GroupID)
		assert.Equal(t, "latest", config.AutoOffsetReset)
		assert.Equal(t, "roundrobin", config.RebalanceStrategy)
		assert.True(t, config.EnableAutoCommit)
	})

	t.Run("Default Admin Config", func(t *testing.T) {
		config := port.DefaultAdminConfig()
		require.NotNil(t, config)

		err := config.Validate()
		assert.NoError(t, err)

		assert.NotEmpty(t, config.Brokers)
		assert.True(t, config.RequestTimeout > 0)
		assert.Equal(t, "PLAINTEXT", config.SecurityProtocol)
	})
}

// BenchmarkProducerConfigValidation benchmarks producer configuration validation.
func BenchmarkProducerConfigValidation(b *testing.B) {
	config := port.DefaultProducerConfig()

	b.ResetTimer()
	for i := 0; i < b.N; i++ {
		_ = config.Validate()
	}
}

// BenchmarkConsumerConfigValidation benchmarks consumer configuration validation.
func BenchmarkConsumerConfigValidation(b *testing.B) {
	config := port.DefaultConsumerConfig()

	b.ResetTimer()
	for i := 0; i < b.N; i++ {
		_ = config.Validate()
	}
}

// BenchmarkAdminConfigValidation benchmarks admin configuration validation.
func BenchmarkAdminConfigValidation(b *testing.B) {
	config := port.DefaultAdminConfig()

	b.ResetTimer()
	for i := 0; i < b.N; i++ {
		_ = config.Validate()
	}
}
