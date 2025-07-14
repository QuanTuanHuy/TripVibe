//go:build integration
// +build integration

// Package service_test provides integration tests for Kafka services.
// These tests require a running Kafka cluster and are tagged with "integration".
// Run with: go test -tags=integration
package service_test

import (
	"context"
	"fmt"
	"sync"
	"testing"
	"time"

	"github.com/quantuanhuy/lib/src/core/port"
	"github.com/quantuanhuy/lib/src/infrastructure/service"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/require"
	"go.uber.org/zap/zaptest"
)

const (
	testBroker  = "localhost:9092"
	testTopic   = "test-topic-integration"
	testGroupID = "test-group-integration"
)

// TestKafkaIntegration_ProducerConsumerWorkflow tests the complete producer-consumer workflow.
func TestKafkaIntegration_ProducerConsumerWorkflow(t *testing.T) {
	logger := zaptest.NewLogger(t)
	ctx := context.Background()

	// Create admin client to set up topic
	adminConfig := &port.AdminConfig{
		Brokers:        []string{testBroker},
		RequestTimeout: 30 * time.Second,
		RetryMax:       3,
		RetryBackoff:   100 * time.Millisecond,
	}

	admin, err := service.NewKafkaAdmin(adminConfig, logger)
	require.NoError(t, err)
	defer admin.Close()

	// Create test topic
	topicConfig := &port.TopicConfig{
		Name:              testTopic,
		NumPartitions:     3,
		ReplicationFactor: 1,
	}

	err = admin.CreateTopic(ctx, topicConfig)
	if err != nil {
		// Topic might already exist, which is fine
		t.Logf("Topic creation failed (might already exist): %v", err)
	}

	// Wait for topic to be ready
	time.Sleep(2 * time.Second)

	// Create producer
	producerConfig := &port.ProducerConfig{
		Brokers:         []string{testBroker},
		RequiredAcks:    1,
		RetryMax:        3,
		RetryBackoff:    100 * time.Millisecond,
		CompressionType: "none",
	}

	producer, err := service.NewKafkaProducer(producerConfig, logger)
	require.NoError(t, err)
	defer producer.Close()

	// Create consumer
	consumerConfig := &port.ConsumerConfig{
		Brokers:            []string{testBroker},
		GroupID:            testGroupID,
		AutoOffsetReset:    "earliest",
		EnableAutoCommit:   true,
		AutoCommitInterval: 1000 * time.Millisecond,
		SessionTimeout:     30 * time.Second,
		HeartbeatInterval:  3 * time.Second,
	}

	consumer, err := service.NewKafkaConsumer(consumerConfig, logger)
	require.NoError(t, err)
	defer consumer.Close()

	// Test data
	testMessages := []*port.KafkaMessage{
		{
			Topic:   testTopic,
			Key:     []byte("key1"),
			Value:   []byte("Hello, Kafka!"),
			Headers: map[string][]byte{"test": []byte("integration")},
		},
		{
			Topic: testTopic,
			Key:   []byte("key2"),
			Value: []byte("Second message"),
		},
		{
			Topic: testTopic,
			Key:   []byte("key3"),
			Value: []byte("Third message"),
		},
	}

	// Send messages
	for _, msg := range testMessages {
		err := producer.SendMessage(ctx, msg)
		require.NoError(t, err)
		t.Logf("Sent message to partition %d, offset %d", msg.Partition, msg.Offset)
	}

	// Flush producer to ensure all messages are sent
	err = producer.Flush(ctx)
	require.NoError(t, err)

	// Set up message collection
	var receivedMessages []*port.KafkaMessage
	var mu sync.Mutex
	messageHandler := func(message *port.KafkaMessage) error {
		mu.Lock()
		receivedMessages = append(receivedMessages, message)
		mu.Unlock()
		t.Logf("Received message: %s from partition %d, offset %d",
			string(message.Value), message.Partition, message.Offset)
		return nil
	}

	// Start consuming
	consumerCtx, cancel := context.WithTimeout(ctx, 30*time.Second)
	defer cancel()

	go func() {
		err := consumer.Subscribe(consumerCtx, []string{testTopic}, messageHandler)
		if err != nil {
			t.Logf("Consumer error: %v", err)
		}
	}()

	// Wait for messages to be consumed
	time.Sleep(10 * time.Second)

	// Verify received messages
	mu.Lock()
	assert.GreaterOrEqual(t, len(receivedMessages), len(testMessages),
		"Should receive at least as many messages as sent")

	// Check that we received the expected messages (might include duplicates or messages from previous runs)
	messageValues := make(map[string]bool)
	for _, msg := range receivedMessages {
		messageValues[string(msg.Value)] = true
	}

	expectedValues := []string{"Hello, Kafka!", "Second message", "Third message"}
	for _, expected := range expectedValues {
		assert.True(t, messageValues[expected],
			"Should receive message with value: %s", expected)
	}
	mu.Unlock()

	// Clean up topic
	err = admin.DeleteTopic(ctx, testTopic)
	if err != nil {
		t.Logf("Topic deletion failed: %v", err)
	}
}

// TestKafkaIntegration_ProducerBatch tests batch message sending.
func TestKafkaIntegration_ProducerBatch(t *testing.T) {
	logger := zaptest.NewLogger(t)
	ctx := context.Background()

	batchTopic := testTopic + "-batch"

	// Create admin client
	adminConfig := port.DefaultAdminConfig()
	adminConfig.Brokers = []string{testBroker}

	admin, err := service.NewKafkaAdmin(adminConfig, logger)
	require.NoError(t, err)
	defer admin.Close()

	// Create test topic
	topicConfig := &port.TopicConfig{
		Name:              batchTopic,
		NumPartitions:     1,
		ReplicationFactor: 1,
	}

	err = admin.CreateTopic(ctx, topicConfig)
	if err != nil {
		t.Logf("Topic creation failed (might already exist): %v", err)
	}

	time.Sleep(2 * time.Second)

	// Create producer
	producerConfig := port.DefaultProducerConfig()
	producerConfig.Brokers = []string{testBroker}
	producerConfig.FlushMessages = 10 // Batch size

	producer, err := service.NewKafkaProducer(producerConfig, logger)
	require.NoError(t, err)
	defer producer.Close()

	// Create batch of messages
	batchSize := 100
	messages := make([]*port.KafkaMessage, batchSize)
	for i := 0; i < batchSize; i++ {
		messages[i] = &port.KafkaMessage{
			Topic: batchTopic,
			Key:   []byte(fmt.Sprintf("key-%d", i)),
			Value: []byte(fmt.Sprintf("message-%d", i)),
		}
	}

	// Send batch
	start := time.Now()
	err = producer.SendMessages(ctx, messages)
	duration := time.Since(start)

	require.NoError(t, err)
	t.Logf("Sent %d messages in %v (%.2f msg/sec)",
		batchSize, duration, float64(batchSize)/duration.Seconds())

	// Verify all messages have partition and offset assigned
	for i, msg := range messages {
		assert.GreaterOrEqual(t, msg.Partition, int32(0), "Message %d should have valid partition", i)
		assert.GreaterOrEqual(t, msg.Offset, int64(0), "Message %d should have valid offset", i)
	}

	// Clean up
	err = admin.DeleteTopic(ctx, batchTopic)
	if err != nil {
		t.Logf("Topic deletion failed: %v", err)
	}
}

// TestKafkaIntegration_AdminOperations tests administrative operations.
func TestKafkaIntegration_AdminOperations(t *testing.T) {
	logger := zaptest.NewLogger(t)
	ctx := context.Background()

	adminTopic := testTopic + "-admin"

	// Create admin client
	adminConfig := port.DefaultAdminConfig()
	adminConfig.Brokers = []string{testBroker}

	admin, err := service.NewKafkaAdmin(adminConfig, logger)
	require.NoError(t, err)
	defer admin.Close()

	// Test topic creation
	topicConfig := &port.TopicConfig{
		Name:              adminTopic,
		NumPartitions:     2,
		ReplicationFactor: 1,
		ConfigEntries: map[string]*string{
			"cleanup.policy": stringPtr("delete"),
		},
	}

	err = admin.CreateTopic(ctx, topicConfig)
	require.NoError(t, err)

	time.Sleep(2 * time.Second)

	// Test topic listing
	topics, err := admin.ListTopics(ctx)
	require.NoError(t, err)
	assert.Contains(t, topics, adminTopic)
	t.Logf("Found %d topics", len(topics))

	// Test topic description
	description, err := admin.DescribeTopic(ctx, adminTopic)
	require.NoError(t, err)
	assert.Equal(t, adminTopic, description.Name)
	assert.Equal(t, int32(2), description.NumPartitions)
	assert.Equal(t, int16(1), description.ReplicationFactor)
	assert.Len(t, description.Partitions, 2)

	t.Logf("Topic %s has %d partitions", description.Name, description.NumPartitions)

	// Test adding partitions
	err = admin.CreatePartitions(ctx, adminTopic, 3)
	require.NoError(t, err)

	time.Sleep(2 * time.Second)

	// Verify partition increase
	description, err = admin.DescribeTopic(ctx, adminTopic)
	require.NoError(t, err)
	assert.Equal(t, int32(3), description.NumPartitions)

	// Test cluster metadata
	metadata, err := admin.GetClusterMetadata(ctx)
	require.NoError(t, err)
	assert.NotEmpty(t, metadata.Brokers)
	assert.Contains(t, metadata.Topics, adminTopic)

	t.Logf("Cluster has %d brokers and %d topics", len(metadata.Brokers), len(metadata.Topics))

	// Test consumer group operations
	groups, err := admin.ListConsumerGroups(ctx)
	require.NoError(t, err)
	t.Logf("Found %d consumer groups", len(groups))

	// Clean up
	err = admin.DeleteTopic(ctx, adminTopic)
	require.NoError(t, err)
}

// TestKafkaIntegration_ConsumerGroupOperations tests consumer group management.
func TestKafkaIntegration_ConsumerGroupOperations(t *testing.T) {
	logger := zaptest.NewLogger(t)
	ctx := context.Background()

	groupTopic := testTopic + "-group"
	groupID := testGroupID + "-operations"

	// Create admin client
	adminConfig := port.DefaultAdminConfig()
	adminConfig.Brokers = []string{testBroker}

	admin, err := service.NewKafkaAdmin(adminConfig, logger)
	require.NoError(t, err)
	defer admin.Close()

	// Create test topic
	topicConfig := &port.TopicConfig{
		Name:              groupTopic,
		NumPartitions:     1,
		ReplicationFactor: 1,
	}

	err = admin.CreateTopic(ctx, topicConfig)
	if err != nil {
		t.Logf("Topic creation failed (might already exist): %v", err)
	}

	time.Sleep(2 * time.Second)

	// Create consumer to establish group
	consumerConfig := &port.ConsumerConfig{
		Brokers:            []string{testBroker},
		GroupID:            groupID,
		AutoOffsetReset:    "earliest",
		EnableAutoCommit:   true,
		AutoCommitInterval: 1000 * time.Millisecond,
	}

	consumer, err := service.NewKafkaConsumer(consumerConfig, logger)
	require.NoError(t, err)

	// Start consumer briefly to create the group
	consumerCtx, cancel := context.WithTimeout(ctx, 5*time.Second)
	go func() {
		consumer.Subscribe(consumerCtx, []string{groupTopic}, func(msg *port.KafkaMessage) error {
			return nil
		})
	}()

	time.Sleep(3 * time.Second)
	cancel()
	consumer.Close()

	// Wait for group to stabilize
	time.Sleep(2 * time.Second)

	// Test consumer group listing
	groups, err := admin.ListConsumerGroups(ctx)
	require.NoError(t, err)

	found := false
	for _, group := range groups {
		if group == groupID {
			found = true
			break
		}
	}
	if found {
		t.Logf("Consumer group %s found in list", groupID)

		// Test consumer group description
		description, err := admin.DescribeConsumerGroup(ctx, groupID)
		if err == nil {
			assert.Equal(t, groupID, description.GroupID)
			t.Logf("Consumer group %s has state: %s", groupID, description.State)
		} else {
			t.Logf("Could not describe consumer group: %v", err)
		}

		// Test offset listing
		offsets, err := admin.ListConsumerGroupOffsets(ctx, groupID, []string{groupTopic})
		if err == nil {
			t.Logf("Retrieved offsets for group %s: %+v", groupID, offsets)
		} else {
			t.Logf("Could not list offsets: %v", err)
		}
	} else {
		t.Logf("Consumer group %s not found (this is normal if the group was cleaned up)", groupID)
	}

	// Clean up
	err = admin.DeleteTopic(ctx, groupTopic)
	if err != nil {
		t.Logf("Topic deletion failed: %v", err)
	}
}

// Helper function to create string pointer
func stringPtr(s string) *string {
	return &s
}

// BenchmarkKafkaIntegration_ProducerThroughput benchmarks producer throughput.
func BenchmarkKafkaIntegration_ProducerThroughput(b *testing.B) {
	logger := zaptest.NewLogger(b)
	ctx := context.Background()

	throughputTopic := testTopic + "-throughput"

	// Create producer
	producerConfig := port.DefaultProducerConfig()
	producerConfig.Brokers = []string{testBroker}
	producerConfig.FlushMessages = 1000
	producerConfig.FlushBytes = 1024 * 1024 // 1MB

	producer, err := service.NewKafkaProducer(producerConfig, logger)
	if err != nil {
		b.Skipf("Could not create producer: %v", err)
	}
	defer producer.Close()

	// Test message
	message := &port.KafkaMessage{
		Topic: throughputTopic,
		Key:   []byte("benchmark-key"),
		Value: []byte("This is a benchmark message for throughput testing"),
	}

	b.ResetTimer()

	for i := 0; i < b.N; i++ {
		message.Key = []byte(fmt.Sprintf("key-%d", i))
		err := producer.SendMessage(ctx, message)
		if err != nil {
			b.Fatalf("Failed to send message: %v", err)
		}
	}

	// Flush remaining messages
	producer.Flush(ctx)
}
