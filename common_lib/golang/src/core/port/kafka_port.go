// Package port defines the interfaces for Kafka operations in the application.
// These interfaces abstract Kafka functionality and allow for easy testing and mocking.
package port

import (
	"context"
	"fmt"
	"strings"
	"time"
)

// KafkaMessage represents a message to be sent to or received from Kafka.
type KafkaMessage struct {
	// Topic is the Kafka topic name
	Topic string

	// Key is the message key for partitioning (optional)
	Key []byte

	// Value is the message payload
	Value []byte

	// Headers contains optional message headers
	Headers map[string][]byte

	// Timestamp is the message timestamp
	Timestamp time.Time

	// Partition is the target partition (for producer) or source partition (for consumer)
	Partition int32

	// Offset is the message offset (for consumer)
	Offset int64
}

// ProducerConfig contains configuration for Kafka producer.
type ProducerConfig struct {
	// Brokers is the list of Kafka broker addresses
	Brokers []string

	// RequiredAcks specifies the number of acknowledgments required
	// 0: No acknowledgment, 1: Leader acknowledgment, -1: All replicas acknowledgment
	RequiredAcks int16

	// MaxMessageBytes is the maximum message size
	MaxMessageBytes int

	// CompressionType specifies the compression algorithm
	// Options: "none", "gzip", "snappy", "lz4", "zstd"
	CompressionType string

	// RetryMax is the maximum number of retries
	RetryMax int

	// RetryBackoff is the backoff time between retries
	RetryBackoff time.Duration

	// FlushFrequency is how often to flush batched messages
	FlushFrequency time.Duration

	// FlushMessages is the number of messages to batch before flushing
	FlushMessages int

	// FlushBytes is the number of bytes to batch before flushing
	FlushBytes int

	// EnableIdempotence ensures exactly-once semantics
	EnableIdempotence bool

	// TransactionID for transactional producer
	TransactionID string

	// SecurityProtocol for authentication ("PLAINTEXT", "SASL_PLAINTEXT", "SASL_SSL", "SSL")
	SecurityProtocol string

	// SASLMechanism for SASL authentication ("PLAIN", "SCRAM-SHA-256", "SCRAM-SHA-512")
	SASLMechanism string

	// SASLUsername for SASL authentication
	SASLUsername string

	// SASLPassword for SASL authentication
	SASLPassword string
}

// ConsumerConfig contains configuration for Kafka consumer.
type ConsumerConfig struct {
	// Brokers is the list of Kafka broker addresses
	Brokers []string

	// GroupID is the consumer group ID
	GroupID string

	// AutoOffsetReset specifies the initial offset when no offset is stored
	// Options: "earliest", "latest"
	AutoOffsetReset string

	// EnableAutoCommit enables automatic offset commits
	EnableAutoCommit bool

	// AutoCommitInterval is the interval for automatic commits
	AutoCommitInterval time.Duration

	// SessionTimeout is the consumer session timeout
	SessionTimeout time.Duration

	// HeartbeatInterval is the heartbeat interval
	HeartbeatInterval time.Duration

	// RebalanceStrategy specifies the partition assignment strategy
	// Options: "range", "roundrobin", "sticky"
	RebalanceStrategy string

	// FetchMinBytes is the minimum number of bytes to fetch in a request
	FetchMinBytes int32

	// FetchMaxBytes is the maximum number of bytes to fetch in a request
	FetchMaxBytes int32

	// FetchMaxWait is the maximum time to wait for a fetch request
	FetchMaxWait time.Duration

	// SecurityProtocol for authentication
	SecurityProtocol string

	// SASLMechanism for SASL authentication
	SASLMechanism string

	// SASLUsername for SASL authentication
	SASLUsername string

	// SASLPassword for SASL authentication
	SASLPassword string
}

// AdminConfig contains configuration for Kafka admin operations.
type AdminConfig struct {
	// Brokers is the list of Kafka broker addresses
	Brokers []string

	// RequestTimeout is the timeout for admin requests
	RequestTimeout time.Duration

	// RetryMax is the maximum number of retries
	RetryMax int

	// RetryBackoff is the backoff time between retries
	RetryBackoff time.Duration

	// SecurityProtocol for authentication
	SecurityProtocol string

	// SASLMechanism for SASL authentication
	SASLMechanism string

	// SASLUsername for SASL authentication
	SASLUsername string

	// SASLPassword for SASL authentication
	SASLPassword string
}

// TopicConfig contains configuration for creating topics.
type TopicConfig struct {
	Name              string             `json:"name" yaml:"name" mapstructure:"name"`
	NumPartitions     int32              `json:"num_partitions" yaml:"num_partitions" mapstructure:"num_partitions"`
	ReplicationFactor int16              `json:"replication_factor" yaml:"replication_factor" mapstructure:"replication_factor"`
	ConfigEntries     map[string]*string `json:"config_entries,omitempty" yaml:"config_entries,omitempty" mapstructure:"config_entries"`
}

// TopicDescription provides detailed information about a Kafka topic.
type TopicDescription struct {
	Name              string                 `json:"name"`
	NumPartitions     int32                  `json:"num_partitions"`
	ReplicationFactor int16                  `json:"replication_factor"`
	Partitions        []PartitionDescription `json:"partitions"`
	ConfigEntries     map[string]*string     `json:"config_entries,omitempty"`
}

// PartitionDescription provides information about a topic partition.
type PartitionDescription struct {
	Partition int32   `json:"partition"`
	Leader    int32   `json:"leader"`
	Replicas  []int32 `json:"replicas"`
	ISR       []int32 `json:"isr"` // In-Sync Replicas
}

// ConsumerGroupDescription provides detailed information about a consumer group.
type ConsumerGroupDescription struct {
	GroupID      string              `json:"group_id"`
	State        string              `json:"state"`
	ProtocolType string              `json:"protocol_type"`
	Protocol     string              `json:"protocol"`
	Members      []MemberDescription `json:"members"`
	Coordinator  int32               `json:"coordinator"`
}

// MemberDescription provides information about a consumer group member.
type MemberDescription struct {
	MemberID         string `json:"member_id"`
	ClientID         string `json:"client_id"`
	ClientHost       string `json:"client_host"`
	MemberMetadata   []byte `json:"member_metadata,omitempty"`
	MemberAssignment []byte `json:"member_assignment,omitempty"`
}

// ClusterMetadata provides information about the Kafka cluster.
type ClusterMetadata struct {
	Brokers []BrokerInfo `json:"brokers"`
	Topics  []string     `json:"topics"`
}

// BrokerInfo provides information about a Kafka broker.
type BrokerInfo struct {
	ID   int32  `json:"id"`
	Host string `json:"host"`
}

// MessageHandler defines the function signature for processing consumed messages.
type MessageHandler func(message *KafkaMessage) error

// IKafkaProducerPort defines the contract for Kafka producer operations.
type IKafkaProducerPort interface {
	// SendMessage sends a single message to Kafka synchronously.
	// Returns an error if the message cannot be sent.
	SendMessage(ctx context.Context, message *KafkaMessage) error

	// SendMessageAsync sends a single message to Kafka asynchronously.
	// Returns immediately and uses callbacks for success/error handling.
	SendMessageAsync(ctx context.Context, message *KafkaMessage, successCallback func(*KafkaMessage), errorCallback func(*KafkaMessage, error)) error

	// SendMessages sends multiple messages to Kafka in a batch.
	// This is more efficient than sending messages individually.
	SendMessages(ctx context.Context, messages []*KafkaMessage) error

	// SendMessagesAsync sends multiple messages to Kafka asynchronously.
	SendMessagesAsync(ctx context.Context, messages []*KafkaMessage, successCallback func([]*KafkaMessage), errorCallback func([]*KafkaMessage, error)) error

	// SendMessageToPartition sends a message to a specific partition.
	SendMessageToPartition(ctx context.Context, message *KafkaMessage, partition int32) error

	// BeginTransaction starts a new transaction (for transactional producer).
	BeginTransaction(ctx context.Context) error

	// CommitTransaction commits the current transaction.
	CommitTransaction(ctx context.Context) error

	// AbortTransaction aborts the current transaction.
	AbortTransaction(ctx context.Context) error

	// GetMetadata returns metadata about Kafka topics and partitions.
	GetMetadata(ctx context.Context, topics []string) (map[string][]int32, error)

	// Flush flushes any pending messages.
	Flush(ctx context.Context) error

	// Close closes the producer and releases resources.
	Close() error
}

// IKafkaConsumerPort defines the contract for Kafka consumer operations.
type IKafkaConsumerPort interface {
	// Subscribe subscribes to topics and starts consuming messages.
	Subscribe(ctx context.Context, topics []string, handler MessageHandler) error

	// SubscribePattern subscribes to topics matching a pattern.
	SubscribePattern(ctx context.Context, pattern string, handler MessageHandler) error

	// Poll polls for messages with timeout.
	Poll(ctx context.Context, timeout time.Duration) ([]*KafkaMessage, error)

	// CommitOffsets commits the specified offsets.
	CommitOffsets(ctx context.Context, offsets map[string]map[int32]int64) error

	// GetOffsets gets the current committed offsets.
	GetOffsets(ctx context.Context, topics []string) (map[string]map[int32]int64, error)

	// Pause pauses consumption from specified topic partitions.
	Pause(ctx context.Context, topicPartitions map[string][]int32) error

	// Resume resumes consumption from specified topic partitions.
	Resume(ctx context.Context, topicPartitions map[string][]int32) error

	// SeekToOffset seeks to a specific offset for topic partitions.
	SeekToOffset(ctx context.Context, offsets map[string]map[int32]int64) error

	// SeekToBeginning seeks to the beginning of topic partitions.
	SeekToBeginning(ctx context.Context, topicPartitions map[string][]int32) error

	// SeekToEnd seeks to the end of topic partitions.
	SeekToEnd(ctx context.Context, topicPartitions map[string][]int32) error

	// Close closes the consumer and releases resources.
	Close() error
}

// IKafkaAdminPort defines the contract for Kafka administrative operations.
type IKafkaAdminPort interface {
	// CreateTopic creates a new topic with the specified configuration.
	CreateTopic(ctx context.Context, topicConfig *TopicConfig) error

	// DeleteTopic deletes a topic.
	DeleteTopic(ctx context.Context, topicName string) error

	// ListTopics lists all topics in the cluster.
	ListTopics(ctx context.Context) ([]string, error)

	// DescribeTopic gets detailed information about a topic.
	DescribeTopic(ctx context.Context, topicName string) (*TopicDescription, error)

	// AlterTopicConfig alters the configuration of a topic.
	AlterTopicConfig(ctx context.Context, topicName string, configEntries map[string]*string) error

	// CreatePartitions adds partitions to an existing topic.
	CreatePartitions(ctx context.Context, topicName string, numPartitions int32) error

	// ListConsumerGroups lists all consumer groups in the cluster.
	ListConsumerGroups(ctx context.Context) ([]string, error)

	// DescribeConsumerGroup gets detailed information about a consumer group.
	DescribeConsumerGroup(ctx context.Context, groupID string) (*ConsumerGroupDescription, error)

	// DeleteConsumerGroup deletes a consumer group.
	DeleteConsumerGroup(ctx context.Context, groupID string) error

	// ListConsumerGroupOffsets lists the committed offsets for a consumer group.
	ListConsumerGroupOffsets(ctx context.Context, groupID string, topics []string) (map[string]map[int32]int64, error)

	// ResetConsumerGroupOffsets resets the offsets for a consumer group.
	ResetConsumerGroupOffsets(ctx context.Context, groupID string, offsets map[string]map[int32]int64) error

	// GetClusterMetadata returns cluster metadata information.
	GetClusterMetadata(ctx context.Context) (*ClusterMetadata, error)

	// Close closes the admin client and releases resources.
	Close() error
}

// DefaultProducerConfig returns a default producer configuration.
func DefaultProducerConfig() *ProducerConfig {
	return &ProducerConfig{
		Brokers:           []string{"localhost:9092"},
		RequiredAcks:      1,
		RetryMax:          3,
		RetryBackoff:      100 * time.Millisecond,
		MaxMessageBytes:   1000000, // 1MB
		CompressionType:   "none",
		FlushFrequency:    100 * time.Millisecond,
		FlushMessages:     100,
		FlushBytes:        16384, // 16KB
		EnableIdempotence: false,
		SecurityProtocol:  "PLAINTEXT",
	}
}

// DefaultConsumerConfig returns a default consumer configuration.
func DefaultConsumerConfig() *ConsumerConfig {
	return &ConsumerConfig{
		Brokers:            []string{"localhost:9092"},
		GroupID:            "default-group",
		AutoOffsetReset:    "latest",
		EnableAutoCommit:   true,
		AutoCommitInterval: 1000 * time.Millisecond,
		SessionTimeout:     30 * time.Second,
		HeartbeatInterval:  3 * time.Second,
		RebalanceStrategy:  "roundrobin",
		FetchMinBytes:      1,
		FetchMaxBytes:      52428800, // 50MB
		FetchMaxWait:       500 * time.Millisecond,
		SecurityProtocol:   "PLAINTEXT",
	}
}

// DefaultAdminConfig returns a default admin configuration.
func DefaultAdminConfig() *AdminConfig {
	return &AdminConfig{
		Brokers:          []string{"localhost:9092"},
		RequestTimeout:   30 * time.Second,
		RetryMax:         3,
		RetryBackoff:     100 * time.Millisecond,
		SecurityProtocol: "PLAINTEXT",
	}
}

// Validate validates the producer configuration.
func (c *ProducerConfig) Validate() error {
	if len(c.Brokers) == 0 {
		return fmt.Errorf("at least one broker must be specified")
	}

	if c.RequiredAcks < -1 || c.RequiredAcks > 1 {
		return fmt.Errorf("required_acks must be -1, 0, or 1")
	}

	if c.RetryMax < 0 {
		return fmt.Errorf("retry_max must be non-negative")
	}

	if c.MaxMessageBytes < 0 {
		return fmt.Errorf("max_message_bytes must be non-negative")
	}

	validCompressionTypes := []string{"none", "gzip", "snappy", "lz4", "zstd"}
	isValid := false
	for _, validType := range validCompressionTypes {
		if strings.ToLower(c.CompressionType) == validType {
			isValid = true
			break
		}
	}
	if !isValid {
		return fmt.Errorf("compression_type must be one of: %v", validCompressionTypes)
	}

	return nil
}

// Validate validates the consumer configuration.
func (c *ConsumerConfig) Validate() error {
	if len(c.Brokers) == 0 {
		return fmt.Errorf("at least one broker must be specified")
	}

	if c.GroupID == "" {
		return fmt.Errorf("group_id must be specified")
	}

	validOffsetResets := []string{"earliest", "latest"}
	isValid := false
	for _, validReset := range validOffsetResets {
		if strings.ToLower(c.AutoOffsetReset) == validReset {
			isValid = true
			break
		}
	}
	if !isValid {
		return fmt.Errorf("auto_offset_reset must be one of: %v", validOffsetResets)
	}

	validStrategies := []string{"range", "roundrobin", "sticky"}
	isValid = false
	for _, validStrategy := range validStrategies {
		if strings.ToLower(c.RebalanceStrategy) == validStrategy {
			isValid = true
			break
		}
	}
	if !isValid {
		return fmt.Errorf("rebalance_strategy must be one of: %v", validStrategies)
	}

	if c.FetchMinBytes < 0 {
		return fmt.Errorf("fetch_min_bytes must be non-negative")
	}

	if c.FetchMaxBytes < 0 {
		return fmt.Errorf("fetch_max_bytes must be non-negative")
	}

	return nil
}

// Validate validates the admin configuration.
func (c *AdminConfig) Validate() error {
	if len(c.Brokers) == 0 {
		return fmt.Errorf("at least one broker must be specified")
	}

	if c.RequestTimeout <= 0 {
		return fmt.Errorf("request_timeout must be positive")
	}

	if c.RetryMax < 0 {
		return fmt.Errorf("retry_max must be non-negative")
	}

	return nil
}

// Validate validates the topic configuration.
func (c *TopicConfig) Validate() error {
	if c.Name == "" {
		return fmt.Errorf("topic name cannot be empty")
	}

	if c.NumPartitions <= 0 {
		return fmt.Errorf("num_partitions must be positive")
	}

	if c.ReplicationFactor <= 0 {
		return fmt.Errorf("replication_factor must be positive")
	}

	return nil
}
