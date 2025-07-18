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
	client          *redis.Client
	callbackManager *CallbackManager
	logger          *zap.Logger
	prefix          string
	ttl             time.Duration
}

func (r *RedisCorrelationManager) StoreCorrelation(ctx context.Context, correlationID string, request *entity.AsyncRequest, callback entity.AsyncCallback) error {
	key := r.GetCorrelationKey(correlationID)

	requestData, err := json.Marshal(request)
	if err != nil {
		return fmt.Errorf("failed to marshal request: %w", err)
	}

	pipe := r.client.Pipeline()
	pipe.HSet(ctx, key, "request", requestData)
	r.callbackManager.StoreCallback(correlationID, callback)
	pipe.Expire(ctx, key, r.ttl)

	_, err = pipe.Exec(ctx)
	if err != nil {
		r.logger.Error("failed to store correlation", zap.String("correlationID", correlationID), zap.Error(err))
		return fmt.Errorf("failed to store correlation: %w", err)
	}

	r.logger.Info("stored correlation", zap.String("correlationID", correlationID))
	return nil
}

func (r *RedisCorrelationManager) GetCorrelation(ctx context.Context, correlationID string) (*entity.AsyncRequest, entity.AsyncCallback, error) {
	key := r.GetCorrelationKey(correlationID)

	result, err := r.client.HGetAll(ctx, key).Result()
	if err != nil {
		if err == redis.Nil {
			return nil, nil, fmt.Errorf("correlation not found: %s", correlationID)
		}
		return nil, nil, fmt.Errorf("failed to get correlation: %w", err)
	}

	if len(result) == 0 {
		return nil, nil, fmt.Errorf("correlation not found: %s", correlationID)
	}

	requestData, ok := result["request"]
	if !ok {
		return nil, nil, fmt.Errorf("request data not found for correlation: %s", correlationID)
	}
	var request entity.AsyncRequest
	if err := json.Unmarshal([]byte(requestData), &request); err != nil {
		return nil, nil, fmt.Errorf("failed to unmarshal request: %w", err)
	}

	callback, ok := r.callbackManager.GetCallback(correlationID)
	if !ok {
		return nil, nil, fmt.Errorf("callback not found for correlation: %s", correlationID)
	}

	return &request, callback, nil
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

func (r *RedisCorrelationManager) RemoveCorrelation(ctx context.Context, correlationID string) error {
	key := r.GetCorrelationKey(correlationID)
	if err := r.client.Del(ctx, key).Err(); err != nil {
		return fmt.Errorf("failed to remove correlation: %w", err)
	}

	r.callbackManager.RemoveCallback(correlationID)
	return nil
}

func (r *RedisCorrelationManager) SetTimeout(ctx context.Context, correlationID string, timeout time.Duration) error {
	panic("unimplemented")
}

func (r *RedisCorrelationManager) CleanupExpired(ctx context.Context) error {
	pattern := fmt.Sprintf("%s:correlation:*", r.prefix)
	var cursor uint64
	var keys []string

	for {
		var scanKeys []string
		var err error

		scanKeys, cursor, err = r.client.Scan(ctx, cursor, pattern, 100).Result()
		if err != nil {
			r.logger.Error("failed to scan for expired correlations", zap.Error(err))
			return fmt.Errorf("failed to scan for expired correlations: %w", err)
		}

		keys = append(keys, scanKeys...)

		if cursor == 0 {
			break
		}
	}

	expiredCount := 0
	for _, key := range keys {
		ttl, err := r.client.TTL(ctx, key).Result()
		if err != nil {
			r.logger.Error("failed to get TTL for key", zap.String("key", key), zap.Error(err))
			continue
		}

		if ttl <= 0 {
			correlationID := r.ExtractCorrelationID(key)
			if correlationID != "" {
				r.client.Del(ctx, key)
				r.callbackManager.RemoveCallback(correlationID)
				expiredCount++
			}
		}
	}

	if expiredCount > 0 {
		r.logger.Info("cleaned up expired correlations", zap.Int("count", expiredCount))
	}
	return nil
}

func (r *RedisCorrelationManager) GetAllPending(ctx context.Context) (map[string]*entity.AsyncRequest, error) {
	pattern := fmt.Sprintf("%s:correlation:*", r.prefix)
	keys, err := r.client.Keys(ctx, pattern).Result()
	if err != nil {
		r.logger.Error("failed to get all pending correlations", zap.Error(err))
		return nil, fmt.Errorf("failed to get all pending correlations: %w", err)
	}

	if len(keys) == 0 {
		return nil, nil
	}

	pending := make(map[string]*entity.AsyncRequest)
	for _, key := range keys {
		correlationID := r.ExtractCorrelationID(key)
		if correlationID == "" {
			r.logger.Warn("invalid correlation key format", zap.String("key", key))
			continue
		}
		result, err := r.client.HGetAll(ctx, key).Result()
		if err != nil {
			r.logger.Error("failed to get correlation data", zap.String("key", key), zap.Error(err))
			continue
		}
		if len(result) == 0 {
			continue
		}

		requestData, ok := result["request"]
		if !ok {
			r.logger.Warn("request data not found for correlation", zap.String("correlationID", correlationID))
			continue
		}
		var request entity.AsyncRequest
		if err := json.Unmarshal([]byte(requestData), &request); err != nil {
			r.logger.Error("failed to unmarshal request data", zap.String("correlationID", correlationID), zap.Error(err))
			continue
		}

		pending[correlationID] = &request
	}

	r.logger.Info("retrieved all pending correlations", zap.Int("count", len(pending)))
	return pending, nil
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

func NewRedisCorrelationManager(client *redis.Client, logger *zap.Logger, prefix string, ttl time.Duration) port.ICorrelationPort {
	return &RedisCorrelationManager{
		client:          client,
		logger:          logger,
		prefix:          prefix,
		ttl:             ttl,
		callbackManager: NewCallbackManager(),
	}
}
