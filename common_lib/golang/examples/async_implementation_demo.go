package main

import (
	"context"
	"log"
	"time"

	"github.com/redis/go-redis/v9"
	"go.uber.org/zap"

	"github.com/quantuanhuy/lib/src/config"
	entity "github.com/quantuanhuy/lib/src/core/entity/async"
	"github.com/quantuanhuy/lib/src/infrastructure/async"
	"github.com/quantuanhuy/lib/src/infrastructure/msg"
)

func main() {
	// Initialize logger
	logger, err := zap.NewDevelopment()
	if err != nil {
		log.Fatal("Failed to create logger:", err)
	}
	defer logger.Sync()

	// Initialize Redis client
	redisClient := redis.NewClient(&redis.Options{
		Addr:     "localhost:6379",
		Password: "",
		DB:       0,
	})

	// Test Redis connection
	ctx := context.Background()
	_, err = redisClient.Ping(ctx).Result()
	if err != nil {
		logger.Error("Failed to connect to Redis", zap.Error(err))
		return
	}

	// Initialize Kafka producer
	producerConfig := &config.ProducerConfig{
		Brokers: []string{"localhost:9092"},
	}
	kafkaProducer, err := msg.NewKafkaProducer(producerConfig, logger)
	if err != nil {
		logger.Error("Failed to create Kafka producer", zap.Error(err))
		return
	}
	defer kafkaProducer.Close()

	// Create async configuration
	asyncConfig := config.DefaultAsyncConfig()
	asyncConfig.RequestTopic = "async-requests"
	asyncConfig.ReplyTopic = "async-replies"

	// Create async factory
	factory := async.NewAsyncFactory(logger, asyncConfig, redisClient, kafkaProducer)

	// Create async request manager
	asyncManager := factory.CreateAsyncRequestManager()

	// Example 1: Send request without callback
	logger.Info("Example 1: Sending request without callback")

	correlationID1, err := asyncManager.SendRequest(ctx, "user-profile", map[string]interface{}{
		"user_id": "12345",
		"action":  "get_profile",
	}, 30*time.Second)

	if err != nil {
		logger.Error("Failed to send request", zap.Error(err))
		return
	}

	// Wait for reply
	reply1, err := asyncManager.WaitForReply(ctx, correlationID1, 30*time.Second)
	if err != nil {
		logger.Error("Failed to wait for reply", zap.Error(err))
	} else {
		logger.Info("Received reply",
			zap.String("correlation_id", reply1.CorrelationID),
			zap.String("status", string(reply1.Status)),
			zap.Any("result", reply1.Result))
	}

	// Example 2: Send request with callback
	logger.Info("Example 2: Sending request with callback")

	callback := func(reply *entity.AsyncReply) {
		logger.Info("Callback received reply",
			zap.String("correlation_id", reply.CorrelationID),
			zap.String("status", string(reply.Status)),
			zap.Any("result", reply.Result),
			zap.Duration("duration", reply.Duration))
	}

	correlationID2, err := asyncManager.SendRequestWithCallback(ctx, "order-status", map[string]interface{}{
		"order_id": "ORD-67890",
		"action":   "get_status",
	}, 30*time.Second, callback)

	if err != nil {
		logger.Error("Failed to send request with callback", zap.Error(err))
		return
	}

	logger.Info("Request sent with callback", zap.String("correlation_id", correlationID2))

	// Example 3: Cancel request
	logger.Info("Example 3: Canceling request")

	correlationID3, err := asyncManager.SendRequest(ctx, "long-running-task", map[string]interface{}{
		"task_id": "TASK-999",
		"action":  "process_large_dataset",
	}, 30*time.Second)

	if err != nil {
		logger.Error("Failed to send request", zap.Error(err))
		return
	}

	// Wait a bit then cancel
	time.Sleep(1 * time.Second)

	if err := asyncManager.CancelRequest(ctx, correlationID3); err != nil {
		logger.Error("Failed to cancel request", zap.Error(err))
	} else {
		logger.Info("Request cancelled successfully", zap.String("correlation_id", correlationID3))
	}

	// Example 4: Start cleanup worker
	logger.Info("Example 4: Starting cleanup worker")
	cleanupCtx, cleanupCancel := context.WithCancel(ctx)

	go factory.StartCleanupWorker(cleanupCtx)

	// Let it run for a bit
	time.Sleep(5 * time.Second)

	// Stop cleanup worker
	cleanupCancel()

	// Example 5: Get metrics
	logger.Info("Example 5: Getting metrics")
	metrics := factory.GetMetrics()
	for component, data := range metrics {
		logger.Info("Component metrics",
			zap.String("component", component),
			zap.Any("data", data))
	}

	// Shutdown
	logger.Info("Shutting down async factory")
	if err := factory.Shutdown(ctx); err != nil {
		logger.Error("Failed to shutdown factory", zap.Error(err))
	}

	logger.Info("Example completed successfully")
}
