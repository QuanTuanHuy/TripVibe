// Package service provides infrastructure implementations for Kafka operations.
// This package contains concrete implementations of the Kafka services defined in the core layer.
package service

import (
	"context"
	"fmt"
	"strings"

	"github.com/IBM/sarama"
	"github.com/quantuanhuy/lib/src/core/port"
	"go.uber.org/zap"
)

// KafkaAdmin provides Kafka administrative operations implementation.
// It implements the IKafkaAdminPort interface and handles all Kafka admin-related operations
// including topic management, consumer group management, and cluster administration.
type KafkaAdmin struct {
	clusterAdmin sarama.ClusterAdmin // Cluster admin for administrative operations
	client       sarama.Client       // Client for metadata operations
	config       *port.AdminConfig   // Admin configuration
	logger       *zap.Logger         // Structured logger for operation tracking
}

// NewKafkaAdmin creates a new instance of KafkaAdmin.
// It initializes cluster admin and client with the provided configuration.
//
// Parameters:
//   - config: Admin configuration containing broker addresses, security settings, etc.
//   - logger: Zap logger for operation logging
//
// Returns:
//   - port.IKafkaAdminPort: Interface implementation for Kafka admin operations
//   - error: nil if successful, otherwise the error that occurred during initialization
//
// Example:
//
//	config := &port.AdminConfig{
//	    Brokers: []string{"localhost:9092"},
//	    RequestTimeout: 30 * time.Second,
//	}
//	admin, err := NewKafkaAdmin(config, logger)
func NewKafkaAdmin(config *port.AdminConfig, logger *zap.Logger) (port.IKafkaAdminPort, error) {
	// Create Sarama configuration
	saramaConfig := sarama.NewConfig()

	// Configure timeouts
	if config.RequestTimeout > 0 {
		saramaConfig.Admin.Timeout = config.RequestTimeout
	}

	// Configure retries
	if config.RetryMax > 0 {
		saramaConfig.Admin.Retry.Max = config.RetryMax
	}
	if config.RetryBackoff > 0 {
		saramaConfig.Admin.Retry.Backoff = config.RetryBackoff
	}

	// Configure security
	if err := configureAdminSecurity(saramaConfig, config); err != nil {
		return nil, fmt.Errorf("failed to configure security: %w", err)
	}

	// Set version for admin operations
	saramaConfig.Version = sarama.V2_6_0_0

	// Create cluster admin
	clusterAdmin, err := sarama.NewClusterAdmin(config.Brokers, saramaConfig)
	if err != nil {
		logger.Error("Failed to create cluster admin", zap.Error(err))
		return nil, fmt.Errorf("failed to create cluster admin: %w", err)
	}

	// Create client for metadata operations
	client, err := sarama.NewClient(config.Brokers, saramaConfig)
	if err != nil {
		logger.Error("Failed to create client", zap.Error(err))
		clusterAdmin.Close()
		return nil, fmt.Errorf("failed to create client: %w", err)
	}

	admin := &KafkaAdmin{
		clusterAdmin: clusterAdmin,
		client:       client,
		config:       config,
		logger:       logger,
	}

	logger.Info("Kafka admin initialized successfully",
		zap.Strings("brokers", config.Brokers))

	return admin, nil
}

// configureAdminSecurity configures SASL and SSL settings for the admin client.
func configureAdminSecurity(saramaConfig *sarama.Config, config *port.AdminConfig) error {
	switch strings.ToUpper(config.SecurityProtocol) {
	case "SASL_PLAINTEXT":
		saramaConfig.Net.SASL.Enable = true
		saramaConfig.Net.SASL.User = config.SASLUsername
		saramaConfig.Net.SASL.Password = config.SASLPassword

		switch strings.ToUpper(config.SASLMechanism) {
		case "PLAIN":
			saramaConfig.Net.SASL.Mechanism = sarama.SASLTypePlaintext
		case "SCRAM-SHA-256":
			saramaConfig.Net.SASL.Mechanism = sarama.SASLTypeSCRAMSHA256
		case "SCRAM-SHA-512":
			saramaConfig.Net.SASL.Mechanism = sarama.SASLTypeSCRAMSHA512
		default:
			return fmt.Errorf("unsupported SASL mechanism: %s", config.SASLMechanism)
		}
	case "SASL_SSL":
		saramaConfig.Net.TLS.Enable = true
		saramaConfig.Net.SASL.Enable = true
		saramaConfig.Net.SASL.User = config.SASLUsername
		saramaConfig.Net.SASL.Password = config.SASLPassword

		switch strings.ToUpper(config.SASLMechanism) {
		case "PLAIN":
			saramaConfig.Net.SASL.Mechanism = sarama.SASLTypePlaintext
		case "SCRAM-SHA-256":
			saramaConfig.Net.SASL.Mechanism = sarama.SASLTypeSCRAMSHA256
		case "SCRAM-SHA-512":
			saramaConfig.Net.SASL.Mechanism = sarama.SASLTypeSCRAMSHA512
		default:
			return fmt.Errorf("unsupported SASL mechanism: %s", config.SASLMechanism)
		}
	case "SSL":
		saramaConfig.Net.TLS.Enable = true
	case "PLAINTEXT":
		// No additional configuration needed
	default:
		if config.SecurityProtocol != "" {
			return fmt.Errorf("unsupported security protocol: %s", config.SecurityProtocol)
		}
	}

	return nil
}

// CreateTopic creates a new topic with the specified configuration.
func (a *KafkaAdmin) CreateTopic(ctx context.Context, topicConfig *port.TopicConfig) error {
	// Validate topic configuration
	if topicConfig.Name == "" {
		return fmt.Errorf("topic name cannot be empty")
	}
	if topicConfig.NumPartitions <= 0 {
		topicConfig.NumPartitions = 1
	}
	if topicConfig.ReplicationFactor <= 0 {
		topicConfig.ReplicationFactor = 1
	}

	// Convert to Sarama topic detail
	topicDetail := &sarama.TopicDetail{
		NumPartitions:     topicConfig.NumPartitions,
		ReplicationFactor: topicConfig.ReplicationFactor,
		ConfigEntries:     topicConfig.ConfigEntries,
	}

	// Create topic
	err := a.clusterAdmin.CreateTopic(topicConfig.Name, topicDetail, false)
	if err != nil {
		a.logger.Error("Failed to create topic",
			zap.String("topic", topicConfig.Name),
			zap.Int32("partitions", topicConfig.NumPartitions),
			zap.Int16("replication_factor", topicConfig.ReplicationFactor),
			zap.Error(err))
		return fmt.Errorf("failed to create topic %s: %w", topicConfig.Name, err)
	}

	a.logger.Info("Topic created successfully",
		zap.String("topic", topicConfig.Name),
		zap.Int32("partitions", topicConfig.NumPartitions),
		zap.Int16("replication_factor", topicConfig.ReplicationFactor))

	return nil
}

// DeleteTopic deletes a topic.
func (a *KafkaAdmin) DeleteTopic(ctx context.Context, topicName string) error {
	if topicName == "" {
		return fmt.Errorf("topic name cannot be empty")
	}

	err := a.clusterAdmin.DeleteTopic(topicName)
	if err != nil {
		a.logger.Error("Failed to delete topic",
			zap.String("topic", topicName),
			zap.Error(err))
		return fmt.Errorf("failed to delete topic %s: %w", topicName, err)
	}

	a.logger.Info("Topic deleted successfully",
		zap.String("topic", topicName))

	return nil
}

// ListTopics lists all topics in the cluster.
func (a *KafkaAdmin) ListTopics(ctx context.Context) ([]string, error) {
	topics, err := a.client.Topics()
	if err != nil {
		a.logger.Error("Failed to list topics", zap.Error(err))
		return nil, fmt.Errorf("failed to list topics: %w", err)
	}

	a.logger.Debug("Listed topics successfully",
		zap.Int("count", len(topics)))

	return topics, nil
}

// DescribeTopic gets detailed information about a topic.
func (a *KafkaAdmin) DescribeTopic(ctx context.Context, topicName string) (*port.TopicDescription, error) {
	if topicName == "" {
		return nil, fmt.Errorf("topic name cannot be empty")
	}

	metadata, err := a.clusterAdmin.DescribeTopics([]string{topicName})
	if err != nil {
		a.logger.Error("Failed to describe topic",
			zap.String("topic", topicName),
			zap.Error(err))
		return nil, fmt.Errorf("failed to describe topic %s: %w", topicName, err)
	}

	if len(metadata) == 0 {
		return nil, fmt.Errorf("topic %s not found", topicName)
	}

	// Get the first (and should be only) topic from the response
	var topicMeta *sarama.TopicMetadata
	for _, meta := range metadata {
		topicMeta = meta
		break
	}

	if topicMeta == nil {
		return nil, fmt.Errorf("topic %s not found", topicName)
	}

	// Get topic configuration
	configResources := []sarama.ConfigResource{
		{
			Type: sarama.TopicResource,
			Name: topicName,
		},
	}

	configs, err := a.clusterAdmin.DescribeConfig(configResources[0])
	if err != nil {
		a.logger.Warn("Failed to get topic config",
			zap.String("topic", topicName),
			zap.Error(err))
	}

	// Convert to topic description
	description := &port.TopicDescription{
		Name:              topicName,
		NumPartitions:     int32(len(topicMeta.Partitions)),
		ReplicationFactor: int16(len(topicMeta.Partitions[0].Replicas)),
		Partitions:        make([]port.PartitionDescription, len(topicMeta.Partitions)),
	}

	// Add partition information
	for i, partition := range topicMeta.Partitions {
		description.Partitions[i] = port.PartitionDescription{
			Partition: partition.ID,
			Leader:    partition.Leader,
			Replicas:  partition.Replicas,
			ISR:       partition.Isr,
		}
	}

	// Add configuration if available
	if configs != nil {
		description.ConfigEntries = make(map[string]*string)
		for _, entry := range configs {
			description.ConfigEntries[entry.Name] = &entry.Value
		}
	}

	a.logger.Debug("Described topic successfully",
		zap.String("topic", topicName),
		zap.Int32("partitions", description.NumPartitions))

	return description, nil
}

// AlterTopicConfig alters the configuration of a topic.
func (a *KafkaAdmin) AlterTopicConfig(ctx context.Context, topicName string, configEntries map[string]*string) error {
	if topicName == "" {
		return fmt.Errorf("topic name cannot be empty")
	}

	// Convert config entries
	entries := make(map[string]*string)
	for key, value := range configEntries {
		entries[key] = value
	}

	err := a.clusterAdmin.AlterConfig(sarama.TopicResource, topicName, entries, false)
	if err != nil {
		a.logger.Error("Failed to alter topic config",
			zap.String("topic", topicName),
			zap.Any("config", configEntries),
			zap.Error(err))
		return fmt.Errorf("failed to alter topic config for %s: %w", topicName, err)
	}

	a.logger.Info("Topic config altered successfully",
		zap.String("topic", topicName),
		zap.Any("config", configEntries))

	return nil
}

// CreatePartitions adds partitions to an existing topic.
func (a *KafkaAdmin) CreatePartitions(ctx context.Context, topicName string, numPartitions int32) error {
	if topicName == "" {
		return fmt.Errorf("topic name cannot be empty")
	}
	if numPartitions <= 0 {
		return fmt.Errorf("number of partitions must be positive")
	}

	err := a.clusterAdmin.CreatePartitions(topicName, numPartitions, nil, false)
	if err != nil {
		a.logger.Error("Failed to create partitions",
			zap.String("topic", topicName),
			zap.Int32("partitions", numPartitions),
			zap.Error(err))
		return fmt.Errorf("failed to create partitions for topic %s: %w", topicName, err)
	}

	a.logger.Info("Partitions created successfully",
		zap.String("topic", topicName),
		zap.Int32("partitions", numPartitions))

	return nil
}

// ListConsumerGroups lists all consumer groups in the cluster.
func (a *KafkaAdmin) ListConsumerGroups(ctx context.Context) ([]string, error) {
	groups, err := a.clusterAdmin.ListConsumerGroups()
	if err != nil {
		a.logger.Error("Failed to list consumer groups", zap.Error(err))
		return nil, fmt.Errorf("failed to list consumer groups: %w", err)
	}

	groupNames := make([]string, 0, len(groups))
	for groupName := range groups {
		groupNames = append(groupNames, groupName)
	}

	a.logger.Debug("Listed consumer groups successfully",
		zap.Int("count", len(groupNames)))

	return groupNames, nil
}

// DescribeConsumerGroup gets detailed information about a consumer group.
func (a *KafkaAdmin) DescribeConsumerGroup(ctx context.Context, groupID string) (*port.ConsumerGroupDescription, error) {
	if groupID == "" {
		return nil, fmt.Errorf("group ID cannot be empty")
	}

	groups, err := a.clusterAdmin.DescribeConsumerGroups([]string{groupID})
	if err != nil {
		a.logger.Error("Failed to describe consumer group",
			zap.String("group_id", groupID),
			zap.Error(err))
		return nil, fmt.Errorf("failed to describe consumer group %s: %w", groupID, err)
	}

	if len(groups) == 0 {
		return nil, fmt.Errorf("consumer group %s not found", groupID)
	}

	// Get the first (and should be only) group from the response
	var group *sarama.GroupDescription
	for _, g := range groups {
		group = g
		break
	}

	if group == nil {
		return nil, fmt.Errorf("consumer group %s not found", groupID)
	}

	// Convert to consumer group description
	description := &port.ConsumerGroupDescription{
		GroupID:      groupID,
		State:        group.State,
		ProtocolType: group.ProtocolType,
		Protocol:     group.Protocol,
		Members:      make([]port.MemberDescription, len(group.Members)),
		Coordinator:  0, // Coordinator ID not available in this version
	}

	// Add member information
	i := 0
	for _, member := range group.Members {
		description.Members[i] = port.MemberDescription{
			MemberID:         member.MemberId,
			ClientID:         member.ClientId,
			ClientHost:       member.ClientHost,
			MemberMetadata:   member.MemberMetadata,
			MemberAssignment: member.MemberAssignment,
		}
		i++
	}

	a.logger.Debug("Described consumer group successfully",
		zap.String("group_id", groupID),
		zap.String("state", description.State))

	return description, nil
}

// DeleteConsumerGroup deletes a consumer group.
func (a *KafkaAdmin) DeleteConsumerGroup(ctx context.Context, groupID string) error {
	if groupID == "" {
		return fmt.Errorf("group ID cannot be empty")
	}

	err := a.clusterAdmin.DeleteConsumerGroup(groupID)
	if err != nil {
		a.logger.Error("Failed to delete consumer group",
			zap.String("group_id", groupID),
			zap.Error(err))
		return fmt.Errorf("failed to delete consumer group %s: %w", groupID, err)
	}

	a.logger.Info("Consumer group deleted successfully",
		zap.String("group_id", groupID))

	return nil
}

// ListConsumerGroupOffsets lists the committed offsets for a consumer group.
func (a *KafkaAdmin) ListConsumerGroupOffsets(ctx context.Context, groupID string, topics []string) (map[string]map[int32]int64, error) {
	if groupID == "" {
		return nil, fmt.Errorf("group ID cannot be empty")
	}

	offsets, err := a.clusterAdmin.ListConsumerGroupOffsets(groupID, map[string][]int32{})
	if err != nil {
		a.logger.Error("Failed to list consumer group offsets",
			zap.String("group_id", groupID),
			zap.Error(err))
		return nil, fmt.Errorf("failed to list consumer group offsets for %s: %w", groupID, err)
	}

	result := make(map[string]map[int32]int64)

	// Filter by requested topics if provided
	for topic, partitions := range offsets.Blocks {
		if len(topics) > 0 {
			found := false
			for _, requestedTopic := range topics {
				if topic == requestedTopic {
					found = true
					break
				}
			}
			if !found {
				continue
			}
		}

		result[topic] = make(map[int32]int64)
		for partition, block := range partitions {
			result[topic][partition] = block.Offset
		}
	}

	a.logger.Debug("Listed consumer group offsets successfully",
		zap.String("group_id", groupID),
		zap.Int("topic_count", len(result)))

	return result, nil
}

// ResetConsumerGroupOffsets resets the offsets for a consumer group.
func (a *KafkaAdmin) ResetConsumerGroupOffsets(ctx context.Context, groupID string, offsets map[string]map[int32]int64) error {
	if groupID == "" {
		return fmt.Errorf("group ID cannot be empty")
	}

	// Note: Direct offset reset is not available in Sarama cluster admin
	// This operation typically requires stopping the consumer group first

	a.logger.Info("Consumer group offset reset requested",
		zap.String("group_id", groupID),
		zap.Any("offsets", offsets))

	return fmt.Errorf("offset reset not directly supported - consumer group must be stopped first")
}

// GetClusterMetadata returns cluster metadata information.
func (a *KafkaAdmin) GetClusterMetadata(ctx context.Context) (*port.ClusterMetadata, error) {
	// Refresh metadata
	if err := a.client.RefreshMetadata(); err != nil {
		a.logger.Error("Failed to refresh metadata", zap.Error(err))
		return nil, fmt.Errorf("failed to refresh metadata: %w", err)
	}

	// Get broker information
	brokers := a.client.Brokers()
	brokerInfo := make([]port.BrokerInfo, len(brokers))
	for i, broker := range brokers {
		brokerInfo[i] = port.BrokerInfo{
			ID:   broker.ID(),
			Host: broker.Addr(),
		}
	}

	// Get topic information
	topics, err := a.client.Topics()
	if err != nil {
		a.logger.Error("Failed to get topics", zap.Error(err))
		return nil, fmt.Errorf("failed to get topics: %w", err)
	}

	metadata := &port.ClusterMetadata{
		Brokers: brokerInfo,
		Topics:  topics,
	}

	a.logger.Debug("Retrieved cluster metadata successfully",
		zap.Int("broker_count", len(brokers)),
		zap.Int("topic_count", len(topics)))

	return metadata, nil
}

// Close closes the admin client and releases resources.
func (a *KafkaAdmin) Close() error {
	var errors []error

	// Close cluster admin
	if err := a.clusterAdmin.Close(); err != nil {
		a.logger.Error("Failed to close cluster admin", zap.Error(err))
		errors = append(errors, fmt.Errorf("cluster admin: %w", err))
	}

	// Close client
	if err := a.client.Close(); err != nil {
		a.logger.Error("Failed to close client", zap.Error(err))
		errors = append(errors, fmt.Errorf("client: %w", err))
	}

	if len(errors) > 0 {
		return fmt.Errorf("failed to close admin components: %v", errors)
	}

	a.logger.Info("Kafka admin closed successfully")
	return nil
}
