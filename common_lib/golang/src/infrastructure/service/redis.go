// Package service provides infrastructure implementations for the core ports.
// This package contains concrete implementations of the services defined in the core layer.
package service

import (
	"context"
	"encoding/json"
	"time"

	"github.com/quantuanhuy/lib/src/core/port"
	"github.com/redis/go-redis/v9"
	"go.uber.org/zap"
)

// RedisService provides Redis operations implementation.
// It implements the IRedisPort interface and handles all Redis-related operations
// including basic key-value operations, hash operations, and distributed locking.
type RedisService struct {
	redisClient *redis.Client // Redis client instance for database operations
	logger      *zap.Logger   // Structured logger for operation tracking and debugging
}

// DeleteKeyFromRedis removes a key from Redis.
// It deletes the specified key and all associated data from Redis.
//
// Parameters:
//   - ctx: Context for request timeout and cancellation
//   - key: The Redis key to delete
//
// Returns:
//   - error: nil if successful, otherwise the error that occurred
//
// Example:
//
//	err := redisService.DeleteKeyFromRedis(ctx, "user:123")
//	if err != nil {
//	    log.Printf("Failed to delete key: %v", err)
//	}
func (r *RedisService) DeleteKeyFromRedis(ctx context.Context, key string) error {
	err := r.redisClient.Del(ctx, key).Err()
	if err != nil {
		r.logger.Error("Failed to delete key from Redis", zap.String("key", key), zap.Error(err))
		return err
	}
	r.logger.Info("Key deleted from Redis", zap.String("key", key))
	return nil
}

// GetFromRedis retrieves and unmarshals a value from Redis.
// It gets the value associated with the key and unmarshals it into the destination.
//
// Parameters:
//   - ctx: Context for request timeout and cancellation
//   - key: The Redis key to retrieve
//   - destination: Pointer to the variable where the unmarshaled value will be stored
//
// Returns:
//   - error: nil if successful, otherwise the error that occurred
//
// Notes:
//   - If the key doesn't exist (redis.Nil), the function returns nil without error
//   - The destination must be a pointer to the expected data type
//
// Example:
//
//	var user User
//	err := redisService.GetFromRedis(ctx, "user:123", &user)
//	if err != nil {
//	    log.Printf("Failed to get user: %v", err)
//	}
func (r *RedisService) GetFromRedis(ctx context.Context, key string, destination interface{}) error {
	data, err := r.redisClient.Get(ctx, key).Result()
	if err != nil {
		if err == redis.Nil {
			r.logger.Warn("Key not found in Redis", zap.String("key", key))
			return nil
		}
		r.logger.Error("Failed to get value from Redis", zap.String("key", key), zap.Error(err))
		return err
	}
	if err := json.Unmarshal([]byte(data), destination); err != nil {
		r.logger.Error("Failed to unmarshal value from Redis", zap.String("key", key), zap.Error(err))
		return err
	}
	r.logger.Info("Value retrieved from Redis", zap.String("key", key))
	return nil
}

// GetHGetToRedis retrieves all fields and values from a Redis hash.
// It returns a map containing all field-value pairs stored in the hash.
//
// Parameters:
//   - ctx: Context for request timeout and cancellation
//   - key: The Redis hash key to retrieve
//
// Returns:
//   - map[string]string: Map of field-value pairs, nil if hash doesn't exist
//   - error: nil if successful, otherwise the error that occurred
//
// Notes:
//   - If the hash doesn't exist (redis.Nil), returns (nil, nil)
//   - All values are returned as strings as per Redis hash behavior
//
// Example:
//
//	fields, err := redisService.GetHGetToRedis(ctx, "user:123:profile")
//	if err != nil {
//	    log.Printf("Failed to get hash: %v", err)
//	}
//	if fields != nil {
//	    name := fields["name"]
//	    email := fields["email"]
//	}
func (r *RedisService) GetHGetToRedis(ctx context.Context, key string) (map[string]string, error) {
	data, err := r.redisClient.HGetAll(ctx, key).Result()
	if err != nil {
		if err == redis.Nil {
			r.logger.Warn("Hash not found in Redis", zap.String("key", key))
			return nil, nil
		}
		r.logger.Error("Failed to get hash from Redis", zap.String("key", key), zap.Error(err))
		return nil, err
	}
	return data, nil
}

// SetHSetToRedis stores multiple field-value pairs in a Redis hash with expiration.
// It sets multiple fields in a hash and applies an expiration time to the entire hash.
//
// Parameters:
//   - ctx: Context for request timeout and cancellation
//   - key: The Redis hash key
//   - mapFieldValue: Map of field-value pairs to store in the hash
//   - expiration: Expiration time in seconds (0 for no expiration)
//
// Returns:
//   - error: nil if successful, otherwise the error that occurred
//
// Notes:
//   - The expiration applies to the entire hash, not individual fields
//   - If expiration is 0, the hash will not expire
//   - Values can be of any type that's compatible with Redis hash fields
//
// Example:
//
//	profile := map[string]interface{}{
//	    "name": "John Doe",
//	    "age": 30,
//	    "email": "john@example.com",
//	}
//	err := redisService.SetHSetToRedis(ctx, "user:123:profile", profile, 3600)
func (r *RedisService) SetHSetToRedis(ctx context.Context, key string, mapFieldValue map[string]interface{}, expiration int64) error {
	err := r.redisClient.HSet(ctx, key, mapFieldValue).Err()
	if err != nil {
		r.logger.Error("Failed to set hash in Redis", zap.String("key", key), zap.Error(err))
		return err
	}

	// Set expiration if specified
	if expiration > 0 {
		err = r.redisClient.Expire(ctx, key, time.Duration(expiration)*time.Second).Err()
		if err != nil {
			r.logger.Error("Failed to set expiration for hash in Redis", zap.String("key", key), zap.Error(err))
			return err
		}
	}

	r.logger.Info("Hash set in Redis", zap.String("key", key), zap.Any("fields", mapFieldValue))
	return nil
}

// SetLock attempts to acquire a distributed lock using Redis SETNX.
// It tries to set a key only if it doesn't exist, implementing a simple distributed lock.
//
// Parameters:
//   - ctx: Context for request timeout and cancellation
//   - key: The lock key name
//   - value: The lock value (typically a unique identifier)
//   - expiration: Lock expiration time in seconds to prevent deadlocks
//
// Returns:
//   - bool: true if lock was successfully acquired, false if lock already exists
//   - error: nil if operation succeeded, otherwise the error that occurred
//
// Notes:
//   - This implements a basic distributed lock mechanism
//   - The expiration prevents deadlocks if the holder fails to release the lock
//   - The value should be unique to identify the lock holder
//
// Example:
//
//	lockID := uuid.New().String()
//	acquired, err := redisService.SetLock(ctx, "process:lock", lockID, 300)
//	if err != nil {
//	    log.Printf("Lock operation failed: %v", err)
//	    return
//	}
//	if !acquired {
//	    log.Println("Could not acquire lock, another process is running")
//	    return
//	}
//	// Process with lock acquired
//	defer redisService.DeleteKeyFromRedis(ctx, "process:lock")
func (r *RedisService) SetLock(ctx context.Context, key string, value string, expiration int64) (bool, error) {
	ok, err := r.redisClient.SetNX(ctx, key, value, time.Duration(expiration)*time.Second).Result()
	if err != nil {
		r.logger.Error("Failed to set lock in Redis", zap.String("key", key), zap.Error(err))
		return false, err
	}
	if ok {
		r.logger.Info("Lock acquired in Redis", zap.String("key", key), zap.String("value", value))
	} else {
		r.logger.Warn("Lock already exists in Redis", zap.String("key", key))
	}
	return ok, nil
}

// SetToRedis stores a value in Redis with JSON serialization and expiration.
// It marshals the value to JSON and stores it with the specified key and expiration.
//
// Parameters:
//   - ctx: Context for request timeout and cancellation
//   - key: The Redis key to store the value under
//   - value: The value to store (will be JSON marshaled)
//   - expiration: Expiration time in seconds (0 for no expiration)
//
// Returns:
//   - error: nil if successful, otherwise the error that occurred
//
// Notes:
//   - The value is automatically JSON marshaled before storage
//   - If expiration is 0, the key will not expire
//   - Any type that can be JSON marshaled is supported
//
// Example:
//
//	user := User{ID: 123, Name: "John Doe", Email: "john@example.com"}
//	err := redisService.SetToRedis(ctx, "user:123", user, 3600)
//	if err != nil {
//	    log.Printf("Failed to store user: %v", err)
//	}
func (r *RedisService) SetToRedis(ctx context.Context, key string, value interface{}, expiration int64) error {
	data, err := json.Marshal(value)
	if err != nil {
		r.logger.Error("Failed to marshal value", zap.Error(err))
		return err
	}
	if err := r.redisClient.Set(ctx, key, data, time.Duration(expiration)*time.Second).Err(); err != nil {
		r.logger.Error("Failed to set value in Redis", zap.String("key", key), zap.Error(err))
		return err
	}
	r.logger.Info("Value set in Redis", zap.String("key", key))
	return nil
}

// NewRedisService creates a new instance of RedisService.
// It initializes the service with the provided Redis client and logger.
//
// Parameters:
//   - redisClient: Configured Redis client instance
//   - logger: Zap logger for operation logging
//
// Returns:
//   - port.IRedisPort: Interface implementation for Redis operations
//
// Example:
//
//	client := redis.NewClient(&redis.Options{
//	    Addr: "localhost:6379",
//	})
//	logger := zap.NewProduction()
//	redisService := NewRedisService(client, logger)
func NewRedisService(redisClient *redis.Client, logger *zap.Logger) port.IRedisPort {
	return &RedisService{
		redisClient: redisClient,
		logger:      logger,
	}
}
