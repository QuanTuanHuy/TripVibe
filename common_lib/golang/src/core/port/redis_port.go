// Package port defines the interfaces for the core business logic layer.
// These interfaces represent the contracts that the infrastructure layer must implement.
package port

import "context"

// IRedisPort defines the contract for Redis operations in the application.
// This interface abstracts Redis functionality and allows for easy testing and mocking.
//
// All methods accept a context for timeout/cancellation control and return errors
// for proper error handling in the application flow.
type IRedisPort interface {
	// SetToRedis stores a value in Redis with JSON serialization and expiration.
	// The value is automatically marshaled to JSON before storage.
	//
	// Parameters:
	//   - ctx: Context for request timeout and cancellation
	//   - key: The Redis key to store the value under
	//   - value: The value to store (must be JSON serializable)
	//   - expiration: Expiration time in seconds (0 for no expiration)
	//
	// Returns:
	//   - error: nil if successful, otherwise the error that occurred
	SetToRedis(ctx context.Context, key string, value interface{}, expiration int64) error

	// GetFromRedis retrieves and unmarshals a value from Redis.
	// The stored JSON value is unmarshaled into the destination parameter.
	//
	// Parameters:
	//   - ctx: Context for request timeout and cancellation
	//   - key: The Redis key to retrieve
	//   - destination: Pointer to the variable where the unmarshaled value will be stored
	//
	// Returns:
	//   - error: nil if successful or key doesn't exist, otherwise the error that occurred
	//
	// Note: If the key doesn't exist, the method returns nil without error
	GetFromRedis(ctx context.Context, key string, destination interface{}) error

	// SetHSetToRedis stores multiple field-value pairs in a Redis hash with expiration.
	// This is useful for storing structured data where you need to access individual fields.
	//
	// Parameters:
	//   - ctx: Context for request timeout and cancellation
	//   - key: The Redis hash key
	//   - mapFieldValue: Map of field-value pairs to store in the hash
	//   - expiration: Expiration time in seconds (0 for no expiration)
	//
	// Returns:
	//   - error: nil if successful, otherwise the error that occurred
	SetHSetToRedis(ctx context.Context, key string, mapFieldValue map[string]interface{}, expiration int64) error

	// GetHGetToRedis retrieves all fields and values from a Redis hash.
	// Returns a map containing all field-value pairs stored in the hash.
	//
	// Parameters:
	//   - ctx: Context for request timeout and cancellation
	//   - key: The Redis hash key to retrieve
	//
	// Returns:
	//   - map[string]string: Map of field-value pairs, nil if hash doesn't exist
	//   - error: nil if successful, otherwise the error that occurred
	//
	// Note: All values are returned as strings as per Redis hash behavior
	GetHGetToRedis(ctx context.Context, key string) (map[string]string, error)

	// DeleteKeyFromRedis removes a key from Redis.
	// This operation deletes the key and all associated data.
	//
	// Parameters:
	//   - ctx: Context for request timeout and cancellation
	//   - key: The Redis key to delete
	//
	// Returns:
	//   - error: nil if successful, otherwise the error that occurred
	//
	// Note: Deleting a non-existent key does not return an error
	DeleteKeyFromRedis(ctx context.Context, key string) error

	// SetLock attempts to acquire a distributed lock using Redis SETNX.
	// This implements a simple distributed lock mechanism for coordinating between processes.
	//
	// Parameters:
	//   - ctx: Context for request timeout and cancellation
	//   - key: The lock key name (should be unique for the resource being locked)
	//   - value: The lock value (typically a unique identifier for the lock holder)
	//   - expiration: Lock expiration time in seconds to prevent deadlocks
	//
	// Returns:
	//   - bool: true if lock was successfully acquired, false if lock already exists
	//   - error: nil if operation succeeded, otherwise the error that occurred
	//
	// Important: Always set an expiration to prevent deadlocks if the lock holder crashes
	SetLock(ctx context.Context, key string, value string, expiration int64) (bool, error)
}
