package async

import (
	"context"
	"encoding/json"
	"fmt"
	"time"

	"github.com/redis/go-redis/v9"
	"go.uber.org/zap"

	entity "github.com/quantuanhuy/lib/src/core/entity/async"
	"github.com/quantuanhuy/lib/src/core/port"
)

// RedisCorrelationManager implements correlation management using Redis
type RedisCorrelationManager struct {
	client          *redis.Client
	logger          *zap.Logger
	prefix          string
	ttl             time.Duration
	callbackManager *CallbackManager
}

// NewRedisCorrelationManager creates a new Redis correlation manager
func NewRedisCorrelationManager(
	client *redis.Client,
	logger *zap.Logger,
	prefix string,
	ttl time.Duration,
) port.ICorrelationManagerPort {
	return &RedisCorrelationManager{
		client:          client,
		logger:          logger,
		prefix:          prefix,
		ttl:             ttl,
		callbackManager: NewCallbackManager(),
	}
}

// StoreCorrelation stores correlation data in Redis
func (r *RedisCorrelationManager) StoreCorrelation(
	ctx context.Context,
	correlationID string,
	request *entity.AsyncRequest,
	callback entity.AsyncCallback,
) error {
	key := r.getCorrelationKey(correlationID)

	// Store the request
	requestData, err := json.Marshal(request)
	if err != nil {
		r.logger.Error("Failed to marshal request",
			zap.String("correlation_id", correlationID),
			zap.Error(err))
		return fmt.Errorf("failed to marshal request: %w", err)
	}

	pipe := r.client.Pipeline()

	// Store request data
	pipe.HSet(ctx, key, "request", requestData)

	// Store callback in memory manager
	r.callbackManager.StoreCallback(correlationID, callback)

	// Set expiration
	pipe.Expire(ctx, key, r.ttl)

	_, err = pipe.Exec(ctx)
	if err != nil {
		r.logger.Error("Failed to store correlation",
			zap.String("correlation_id", correlationID),
			zap.Error(err))
		return fmt.Errorf("failed to store correlation: %w", err)
	}

	r.logger.Debug("Stored correlation",
		zap.String("correlation_id", correlationID),
		zap.Duration("ttl", r.ttl))

	return nil
}

// GetCorrelation retrieves correlation data from Redis
func (r *RedisCorrelationManager) GetCorrelation(
	ctx context.Context,
	correlationID string,
) (*entity.AsyncRequest, entity.AsyncCallback, error) {
	key := r.getCorrelationKey(correlationID)

	result, err := r.client.HGetAll(ctx, key).Result()
	if err != nil {
		if err == redis.Nil {
			return nil, nil, fmt.Errorf("correlation not found: %s", correlationID)
		}
		r.logger.Error("Failed to get correlation",
			zap.String("correlation_id", correlationID),
			zap.Error(err))
		return nil, nil, fmt.Errorf("failed to get correlation: %w", err)
	}

	if len(result) == 0 {
		return nil, nil, fmt.Errorf("correlation not found: %s", correlationID)
	}

	// Parse request data
	requestData, exists := result["request"]
	if !exists {
		return nil, nil, fmt.Errorf("request data not found for correlation: %s", correlationID)
	}

	var request entity.AsyncRequest
	if err := json.Unmarshal([]byte(requestData), &request); err != nil {
		r.logger.Error("Failed to unmarshal request",
			zap.String("correlation_id", correlationID),
			zap.Error(err))
		return nil, nil, fmt.Errorf("failed to unmarshal request: %w", err)
	}

	// Get callback from callback manager
	callback, _ := r.callbackManager.GetCallback(correlationID)

	r.logger.Debug("Retrieved correlation",
		zap.String("correlation_id", correlationID))

	return &request, callback, nil
}

// RemoveCorrelation removes correlation data from Redis
func (r *RedisCorrelationManager) RemoveCorrelation(
	ctx context.Context,
	correlationID string,
) error {
	key := r.getCorrelationKey(correlationID)

	// Remove from Redis
	err := r.client.Del(ctx, key).Err()
	if err != nil {
		r.logger.Error("Failed to remove correlation",
			zap.String("correlation_id", correlationID),
			zap.Error(err))
		return fmt.Errorf("failed to remove correlation: %w", err)
	}

	// Remove callback from memory
	r.callbackManager.RemoveCallback(correlationID)

	r.logger.Debug("Removed correlation",
		zap.String("correlation_id", correlationID))

	return nil
}

// CleanupExpired removes expired correlations from Redis
func (r *RedisCorrelationManager) CleanupExpired(ctx context.Context) error {
	// Redis automatically handles expiration, so we don't need to do much here
	// But we can optionally scan for expired keys and clean them up

	pattern := r.prefix + "*"
	var cursor uint64
	var keys []string

	for {
		var scanKeys []string
		var err error

		scanKeys, cursor, err = r.client.Scan(ctx, cursor, pattern, 100).Result()
		if err != nil {
			r.logger.Error("Failed to scan keys for cleanup", zap.Error(err))
			return fmt.Errorf("failed to scan keys: %w", err)
		}

		keys = append(keys, scanKeys...)

		if cursor == 0 {
			break
		}
	}

	// Check TTL for each key and remove if expired
	expiredCount := 0
	for _, key := range keys {
		ttl, err := r.client.TTL(ctx, key).Result()
		if err != nil {
			continue
		}

		if ttl < 0 {
			// Key is expired or doesn't exist
			correlationID := r.extractCorrelationID(key)
			r.client.Del(ctx, key)
			r.callbackManager.RemoveCallback(correlationID)
			expiredCount++
		}
	}

	if expiredCount > 0 {
		r.logger.Info("Cleaned up expired correlations",
			zap.Int("expired_count", expiredCount))
	}

	return nil
}

// GetPendingCount returns the count of pending correlations
func (r *RedisCorrelationManager) GetPendingCount(ctx context.Context) (int, error) {
	pattern := fmt.Sprintf("%s:correlation:*", r.prefix)
	keys, err := r.client.Keys(ctx, pattern).Result()
	if err != nil {
		r.logger.Error("Failed to get pending correlation count", zap.Error(err))
		return 0, fmt.Errorf("failed to get pending count: %w", err)
	}

	count := len(keys)
	r.logger.Debug("Retrieved pending correlation count", zap.Int("count", count))
	return count, nil
}

// GetAllPending returns all pending correlations
func (r *RedisCorrelationManager) GetAllPending(ctx context.Context) (map[string]*entity.AsyncRequest, error) {
	pattern := fmt.Sprintf("%s:correlation:*", r.prefix)
	keys, err := r.client.Keys(ctx, pattern).Result()
	if err != nil {
		r.logger.Error("Failed to get pending correlations", zap.Error(err))
		return nil, fmt.Errorf("failed to get pending correlations: %w", err)
	}

	pending := make(map[string]*entity.AsyncRequest)

	for _, key := range keys {
		correlationID := r.extractCorrelationID(key)

		result, err := r.client.HGetAll(ctx, key).Result()
		if err != nil {
			r.logger.Warn("Failed to get correlation data",
				zap.String("correlation_id", correlationID),
				zap.Error(err))
			continue
		}

		if len(result) == 0 {
			continue
		}

		requestData, exists := result["request"]
		if !exists {
			continue
		}

		var request entity.AsyncRequest
		if err := json.Unmarshal([]byte(requestData), &request); err != nil {
			r.logger.Warn("Failed to unmarshal request",
				zap.String("correlation_id", correlationID),
				zap.Error(err))
			continue
		}

		pending[correlationID] = &request
	}

	r.logger.Debug("Retrieved all pending correlations", zap.Int("count", len(pending)))
	return pending, nil
}

// SetTimeout sets timeout for a correlation
func (r *RedisCorrelationManager) SetTimeout(
	ctx context.Context,
	correlationID string,
	timeout time.Duration,
) error {
	key := r.getCorrelationKey(correlationID)

	err := r.client.Expire(ctx, key, timeout).Err()
	if err != nil {
		r.logger.Error("Failed to set timeout for correlation",
			zap.String("correlation_id", correlationID),
			zap.Duration("timeout", timeout),
			zap.Error(err))
		return fmt.Errorf("failed to set timeout: %w", err)
	}

	r.logger.Debug("Set timeout for correlation",
		zap.String("correlation_id", correlationID),
		zap.Duration("timeout", timeout))

	return nil
}

// Helper methods
func (r *RedisCorrelationManager) getCorrelationKey(correlationID string) string {
	return fmt.Sprintf("%s:correlation:%s", r.prefix, correlationID)
}

func (r *RedisCorrelationManager) extractCorrelationID(key string) string {
	prefix := fmt.Sprintf("%s:correlation:", r.prefix)
	return key[len(prefix):]
}
