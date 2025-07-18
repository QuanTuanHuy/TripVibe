package main

import (
	"context"
	"log"
	"time"

	"github.com/redis/go-redis/v9"
	"go.uber.org/zap"

	entity "github.com/quantuanhuy/lib/src/core/entity/async"
	"github.com/quantuanhuy/lib/src/core/service"
	"github.com/quantuanhuy/lib/src/infrastructure/msg"
)

// Example usage of the refactored async request-reply system
func main() {
	// Setup logger
	logger, _ := zap.NewProduction()
	defer logger.Sync()

	// Setup Redis client
	redisClient := redis.NewClient(&redis.Options{
		Addr:     "localhost:6379",
		Password: "",
		DB:       0,
	})

	// Setup Kafka producer (mock for example)
	kafkaProducer := &msg.KafkaProducer{}

	// Create async configuration
	asyncConfig := service.CreateDefaultAsyncConfig()

	// Create bootstrap
	bootstrap := service.NewAsyncSystemBootstrap(
		logger,
		asyncConfig,
		redisClient,
		kafkaProducer,
	)

	// Validate configuration
	if err := bootstrap.ValidateConfiguration(); err != nil {
		log.Fatalf("Configuration validation failed: %v", err)
	}

	// Create async system
	asyncSystem := bootstrap.CreateAsyncSystem()

	// Start the system
	ctx := context.Background()
	if err := asyncSystem.Start(ctx); err != nil {
		log.Fatalf("Failed to start async system: %v", err)
	}
	defer asyncSystem.Stop(ctx)

	// Register custom handlers
	requestHandler := asyncSystem.GetRequestHandler()
	
	// Register a custom handler for "user.create" requests
	requestHandler.RegisterHandler("user.create", &UserCreateHandler{
		logger: logger,
	})

	// Register a custom handler for "order.process" requests
	requestHandler.RegisterHandler("order.process", &OrderProcessHandler{
		logger: logger,
	})

	// Example 1: Send request with callback
	requestManager := asyncSystem.GetRequestManager()
	
	correlationID, err := requestManager.SendRequestWithCallback(
		ctx,
		"user.create",
		map[string]interface{}{
			"name":  "John Doe",
			"email": "john@example.com",
		},
		30*time.Second,
		func(reply *entity.AsyncReply) {
			if reply.IsSuccess() {
				logger.Info("User created successfully",
					zap.String("correlation_id", reply.CorrelationID),
					zap.Any("result", reply.Result))
			} else {
				logger.Error("Failed to create user",
					zap.String("correlation_id", reply.CorrelationID),
					zap.String("error", reply.Error))
			}
		},
	)

	if err != nil {
		log.Fatalf("Failed to send request: %v", err)
	}

	logger.Info("Sent user creation request", zap.String("correlation_id", correlationID))

	// Example 2: Send request and wait for reply
	correlationID2, err := requestManager.SendRequest(
		ctx,
		"order.process",
		map[string]interface{}{
			"order_id": "ORDER-123",
			"amount":   100.50,
		},
		30*time.Second,
	)

	if err != nil {
		log.Fatalf("Failed to send order request: %v", err)
	}

	// Wait for the reply
	reply, err := requestManager.WaitForReply(ctx, correlationID2, 30*time.Second)
	if err != nil {
		log.Fatalf("Failed to wait for reply: %v", err)
	}

	if reply.IsSuccess() {
		logger.Info("Order processed successfully",
			zap.String("correlation_id", reply.CorrelationID),
			zap.Any("result", reply.Result))
	} else {
		logger.Error("Failed to process order",
			zap.String("correlation_id", reply.CorrelationID),
			zap.String("error", reply.Error))
	}

	// Example 3: Cancel a request
	correlationID3, err := requestManager.SendRequest(
		ctx,
		"long.running.task",
		map[string]interface{}{
			"task_id": "TASK-456",
		},
		60*time.Second,
	)

	if err != nil {
		log.Fatalf("Failed to send long running task: %v", err)
	}

	// Cancel after 5 seconds
	time.Sleep(5 * time.Second)
	if err := requestManager.CancelRequest(ctx, correlationID3); err != nil {
		logger.Error("Failed to cancel request", zap.Error(err))
	}

	// Health check
	if err := bootstrap.HealthCheck(ctx); err != nil {
		logger.Error("Health check failed", zap.Error(err))
	}

	logger.Info("Async system example completed successfully")
}

// UserCreateHandler handles user creation requests
type UserCreateHandler struct {
	logger *zap.Logger
}

func (h *UserCreateHandler) Handle(request *entity.AsyncRequest) (*entity.AsyncReply, error) {
	h.logger.Info("Processing user creation request",
		zap.String("correlation_id", request.CorrelationID),
		zap.Any("payload", request.Payload))

	// Simulate user creation logic
	time.Sleep(100 * time.Millisecond)

	// Extract user data from payload
	name, _ := request.Payload["name"].(string)
	email, _ := request.Payload["email"].(string)

	// Create reply
	reply := &entity.AsyncReply{
		ID:            "reply-" + request.ID,
		CorrelationID: request.CorrelationID,
		RequestID:     request.ID,
		Status:        entity.AsyncRequestStatusCompleted,
		Result: map[string]interface{}{
			"user_id": "USER-12345",
			"name":    name,
			"email":   email,
			"created_at": time.Now().Format(time.RFC3339),
		},
		ProcessedAt: time.Now(),
		Duration:    100 * time.Millisecond,
	}

	return reply, nil
}

// OrderProcessHandler handles order processing requests
type OrderProcessHandler struct {
	logger *zap.Logger
}

func (h *OrderProcessHandler) Handle(request *entity.AsyncRequest) (*entity.AsyncReply, error) {
	h.logger.Info("Processing order request",
		zap.String("correlation_id", request.CorrelationID),
		zap.Any("payload", request.Payload))

	// Simulate order processing logic
	time.Sleep(200 * time.Millisecond)

	// Extract order data from payload
	orderID, _ := request.Payload["order_id"].(string)
	amount, _ := request.Payload["amount"].(float64)

	// Create reply
	reply := &entity.AsyncReply{
		ID:            "reply-" + request.ID,
		CorrelationID: request.CorrelationID,
		RequestID:     request.ID,
		Status:        entity.AsyncRequestStatusCompleted,
		Result: map[string]interface{}{
			"order_id":     orderID,
			"amount":       amount,
			"status":       "processed",
			"processed_at": time.Now().Format(time.RFC3339),
		},
		ProcessedAt: time.Now(),
		Duration:    200 * time.Millisecond,
	}

	return reply, nil
}
