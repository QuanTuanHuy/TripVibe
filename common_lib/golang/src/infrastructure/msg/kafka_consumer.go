package msg

import (
	"fmt"
	"strings"

	"github.com/IBM/sarama"
	"github.com/quantuanhuy/lib/src/config"
	entity "github.com/quantuanhuy/lib/src/core/entity/msg"
	"go.uber.org/zap"
)

type KafkaConsumer struct {
	consumerGroup sarama.ConsumerGroup
	config        *config.ConsumerConfig
	logger        *zap.Logger
	handlers      map[string]entity.MessageHandler
	running       bool
	// mu            sync.RWMutex
	// cancel        context.CancelFunc
	// wg            sync.WaitGroup
}

func NewKafkaConsumer(config *config.ConsumerConfig, logger *zap.Logger) (*KafkaConsumer, error) {
	saramaConfig := sarama.NewConfig()

	switch strings.ToLower(config.AutoOffsetReset) {
	case "earliest":
		saramaConfig.Consumer.Offsets.Initial = sarama.OffsetOldest
	case "latest":
		saramaConfig.Consumer.Offsets.Initial = sarama.OffsetNewest
	default:
		saramaConfig.Consumer.Offsets.Initial = sarama.OffsetNewest
	}

	saramaConfig.Consumer.Offsets.AutoCommit.Enable = config.EnableAutoCommit
	if config.AutoCommitInterval > 0 {
		saramaConfig.Consumer.Offsets.AutoCommit.Interval = config.AutoCommitInterval
		saramaConfig.Consumer.Offsets.AutoCommit.Enable = true
	}

	if config.SessionTimeout > 0 {
		saramaConfig.Consumer.Group.Session.Timeout = config.SessionTimeout
	}
	if config.HeartbeatInterval > 0 {
		saramaConfig.Consumer.Group.Heartbeat.Interval = config.HeartbeatInterval
	}

	switch strings.ToLower(config.RebalanceStrategy) {
	case "range":
		saramaConfig.Consumer.Group.Rebalance.Strategy = sarama.NewBalanceStrategyRange()
	case "roundrobin":
		saramaConfig.Consumer.Group.Rebalance.Strategy = sarama.NewBalanceStrategyRoundRobin()
	case "sticky":
		saramaConfig.Consumer.Group.Rebalance.Strategy = sarama.NewBalanceStrategySticky()
	default:
		saramaConfig.Consumer.Group.Rebalance.Strategy = sarama.NewBalanceStrategyRoundRobin()
	}

	if config.FetchMinBytes > 0 {
		saramaConfig.Consumer.Fetch.Min = config.FetchMinBytes
	}
	if config.FetchMaxBytes > 0 {
		saramaConfig.Consumer.Fetch.Max = config.FetchMaxBytes
	}
	if config.FetchMaxWait > 0 {
		saramaConfig.Consumer.MaxWaitTime = config.FetchMaxWait
	}

	// Create consumer group
	consumerGroup, err := sarama.NewConsumerGroup(config.Brokers, config.GroupID, saramaConfig)
	if err != nil {
		logger.Error("Failed to create consumer group",
			zap.String("group_id", config.GroupID),
			zap.Error(err))
		return nil, fmt.Errorf("failed to create consumer group: %w", err)
	}

	consumer := &KafkaConsumer{
		consumerGroup: consumerGroup,
		config:        config,
		logger:        logger,
		handlers:      make(map[string]entity.MessageHandler),
		running:       false,
	}

	logger.Info("Kafka consumer initialized successfully",
		zap.Strings("brokers", config.Brokers),
		zap.String("group_id", config.GroupID),
		zap.String("auto_offset_reset", config.AutoOffsetReset))

	return consumer, nil
}
