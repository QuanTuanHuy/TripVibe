package config

import (
	"context"

	"github.com/redis/go-redis/v9"
	"go.uber.org/zap"
)

func NewRedisClient(cfg *AppConfig, logger *zap.Logger) *redis.Client {
	client := redis.NewClient(&redis.Options{
		Addr:     cfg.RedisAddr,
		Password: "",
		DB:       0,
	})

	ctx := context.Background()
	if err := client.Ping(ctx).Err(); err != nil {
		logger.Fatal("Failed to connect to Redis", zap.Error(err))
	}

	logger.Info("Connected to Redis", zap.String("address", cfg.RedisAddr))
	return client
}
