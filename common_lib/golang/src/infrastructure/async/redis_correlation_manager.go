package async

import (
	"context"
	"encoding/json"
	"fmt"
	"strings"
	"time"

	entity "github.com/quantuanhuy/lib/src/core/entity/async"
	"github.com/quantuanhuy/lib/src/core/port"
	"github.com/redis/go-redis/v9"
	"go.uber.org/zap"
)

type RedisCorrelationManager struct {
	client *redis.Client
	logger *zap.Logger
	prefix string
	ttl    time.Duration
}

func NewRedisCorrelationManager(client *redis.Client, logger *zap.Logger, prefix string, ttl time.Duration) port.ICorrelationPort {
	return &RedisCorrelationManager{
		client: client,
		logger: logger,
		prefix: prefix,
		ttl:    ttl,
	}
}

func (r *RedisCorrelationManager) StoreCorrelation(ctx context.Context, correlationID string, data *entity.CorrelationData) error {
	key := r.GetCorrelationKey(correlationID)

	dataBytes, err := json.Marshal(data)
	if err != nil {
		return fmt.Errorf("failed to marshal correlation data: %w", err)
	}

	pipe := r.client.Pipeline()
	pipe.Set(ctx, key, dataBytes, r.ttl)
	
	if _, err := pipe.Exec(ctx); err != nil {
		r.logger.Error("failed to store correlation", 
			zap.String("correlationID", correlationID), 
			zap.Error(err))
		return fmt.Errorf("failed to store correlation: %w", err)
	}

	r.logger.Debug("stored correlation", 
		zap.String("correlationID", correlationID),
		zap.String("requestType", data.RequestType))
	return nil
}

func (r *RedisCorrelationManager) GetCorrelation(ctx context.Context, correlationID string) (*entity.CorrelationData, error) {
	key := r.GetCorrelationKey(correlationID)

	result, err := r.client.Get(ctx, key).Result()
	if err != nil {
		if err == redis.Nil {
			return nil, fmt.Errorf("correlation not found: %s", correlationID)
		}
		return nil, fmt.Errorf("failed to get correlation: %w", err)
	}

	var data entity.CorrelationData
	if err := json.Unmarshal([]byte(result), &data); err != nil {
		return nil, fmt.Errorf("failed to unmarshal correlation data: %w", err)
	}

	return &data, nil
}

func (r *RedisCorrelationManager) UpdateCorrelation(ctx context.Context, correlationID string, data *entity.CorrelationData) error {
	key := r.GetCorrelationKey(correlationID)

	// Check if correlation exists
	exists, err := r.client.Exists(ctx, key).Result()
	if err != nil {
		return fmt.Errorf("failed to check correlation existence: %w", err)
	}
	if exists == 0 {
		return fmt.Errorf("correlation not found: %s", correlationID)
	}

	dataBytes, err := json.Marshal(data)
	if err != nil {
		return fmt.Errorf("failed to marshal correlation data: %w", err)
	}

	// Update with remaining TTL
	ttl, err := r.client.TTL(ctx, key).Result()
	if err != nil {
		return fmt.Errorf("failed to get TTL: %w", err)
	}

	if err := r.client.Set(ctx, key, dataBytes, ttl).Err(); err != nil {
		return fmt.Errorf("failed to update correlation: %w", err)
	}

	r.logger.Debug("updated correlation", 
		zap.String("correlationID", correlationID),
		zap.String("status", string(data.Status)))
	return nil
}

func (r *RedisCorrelationManager) RemoveCorrelation(ctx context.Context, correlationID string) error {
	key := r.GetCorrelationKey(correlationID)
	
	if err := r.client.Del(ctx, key).Err(); err != nil {
		return fmt.Errorf("failed to remove correlation: %w", err)
	}

	r.logger.Debug("removed correlation", zap.String("correlationID", correlationID))
	return nil
}

func (r *RedisCorrelationManager) SetTimeout(ctx context.Context, correlationID string, timeout time.Duration) error {
	key := r.GetCorrelationKey(correlationID)
	
	exists, err := r.client.Exists(ctx, key).Result()
	if err != nil {
		return fmt.Errorf("failed to check correlation existence: %w", err)
	}
	if exists == 0 {
		return fmt.Errorf("correlation not found: %s", correlationID)
	}

	if err := r.client.Expire(ctx, key, timeout).Err(); err != nil {
		return fmt.Errorf("failed to set timeout: %w", err)
	}

	return nil
}

func (r *RedisCorrelationManager) GetPendingCount(ctx context.Context) (int, error) {
	pattern := fmt.Sprintf("%s:correlation:*", r.prefix)
	keys, err := r.client.Keys(ctx, pattern).Result()
	if err != nil {
		r.logger.Error("failed to get pending count", zap.Error(err))
		return 0, fmt.Errorf("failed to get pending count: %w", err)
	}
	return len(keys), nil
}

func (r *RedisCorrelationManager) GetAllPending(ctx context.Context) (map[string]*entity.CorrelationData, error) {
	pattern := fmt.Sprintf("%s:correlation:*", r.prefix)
	keys, err := r.client.Keys(ctx, pattern).Result()
	if err != nil {
		r.logger.Error("failed to get all pending correlations", zap.Error(err))
		return nil, fmt.Errorf("failed to get all pending correlations: %w", err)
	}

	if len(keys) == 0 {
		return make(map[string]*entity.CorrelationData), nil
	}

	pending := make(map[string]*entity.CorrelationData)
	for _, key := range keys {
		correlationID := r.ExtractCorrelationID(key)
		if correlationID == "" {
			r.logger.Warn("invalid correlation key format", zap.String("key", key))
			continue
		}

		data, err := r.GetCorrelation(ctx, correlationID)
		if err != nil {
			r.logger.Error("failed to get correlation data", 
				zap.String("correlationID", correlationID), 
				zap.Error(err))
			continue
		}

		pending[correlationID] = data
	}

	r.logger.Debug("retrieved all pending correlations", zap.Int("count", len(pending)))
	return pending, nil
}

func (r *RedisCorrelationManager) CleanupExpired(ctx context.Context) error {
	pattern := fmt.Sprintf("%s:correlation:*", r.prefix)
	var cursor uint64
	var keys []string
	const batchSize = 100

	// Scan for all correlation keys
	for {
		var scanKeys []string
		var err error

		scanKeys, cursor, err = r.client.Scan(ctx, cursor, pattern, batchSize).Result()
		if err != nil {
			r.logger.Error("failed to scan for expired correlations", zap.Error(err))
			return fmt.Errorf("failed to scan for expired correlations: %w", err)
		}

		keys = append(keys, scanKeys...)

		if cursor == 0 {
			break
		}
	}

	// Check and remove expired keys
	expiredCount := 0
	for _, key := range keys {
		ttl, err := r.client.TTL(ctx, key).Result()
		if err != nil {
			r.logger.Error("failed to get TTL for key", zap.String("key", key), zap.Error(err))
			continue
		}

		// TTL -2 means key doesn't exist, TTL -1 means key exists but no TTL
		if ttl == -2 || ttl <= 0 {
			correlationID := r.ExtractCorrelationID(key)
			if correlationID != "" {
				if err := r.client.Del(ctx, key).Err(); err != nil {
					r.logger.Error("failed to delete expired key", 
						zap.String("key", key), 
						zap.Error(err))
				} else {
					expiredCount++
				}
			}
		}
	}

	if expiredCount > 0 {
		r.logger.Info("cleaned up expired correlations", zap.Int("count", expiredCount))
	}
	return nil
}

func (r *RedisCorrelationManager) GetCorrelationKey(correlationID string) string {
	return fmt.Sprintf("%s:correlation:%s", r.prefix, correlationID)
}

func (r *RedisCorrelationManager) ExtractCorrelationID(key string) string {
	parts := strings.Split(key, ":")
	if len(parts) < 3 || parts[0] != r.prefix || parts[1] != "correlation" {
		return ""
	}
	return parts[2]
}
