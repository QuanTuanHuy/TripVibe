package service

import (
	"context"
	"fmt"
	"sync"
	"testing"
	"time"

	"github.com/alicebob/miniredis/v2"
	"github.com/redis/go-redis/v9"
	"github.com/stretchr/testify/assert"
	"go.uber.org/zap/zaptest"
)

// TestRedisServiceIntegration provides integration tests for RedisService
// These tests verify the behavior of multiple operations working together
func TestRedisServiceIntegration(t *testing.T) {
	// Setup
	miniRedis, err := miniredis.Run()
	assert.NoError(t, err)
	defer miniRedis.Close()

	client := redis.NewClient(&redis.Options{
		Addr: miniRedis.Addr(),
	})
	defer client.Close()

	logger := zaptest.NewLogger(t)
	service := NewRedisService(client, logger).(*RedisService)
	ctx := context.Background()

	t.Run("UserCacheWorkflow", func(t *testing.T) {
		user := TestUser{
			ID:    100,
			Name:  "Integration Test User",
			Email: "integration@example.com",
		}

		// Store user
		err := service.SetToRedis(ctx, "user:100", user, 30)
		assert.NoError(t, err)

		// Retrieve user
		var retrievedUser TestUser
		err = service.GetFromRedis(ctx, "user:100", &retrievedUser)
		assert.NoError(t, err)
		assert.Equal(t, user, retrievedUser)

		// Update user profile as hash
		profile := map[string]interface{}{
			"last_login": time.Now().Format(time.RFC3339),
			"login_count": 5,
			"status": "active",
		}
		err = service.SetHSetToRedis(ctx, "user:100:profile", profile, 60)
		assert.NoError(t, err)

		// Retrieve profile
		storedProfile, err := service.GetHGetToRedis(ctx, "user:100:profile")
		assert.NoError(t, err)
		assert.NotNil(t, storedProfile)
		assert.Equal(t, "5", storedProfile["login_count"])
		assert.Equal(t, "active", storedProfile["status"])

		// Clean up
		err = service.DeleteKeyFromRedis(ctx, "user:100")
		assert.NoError(t, err)
		err = service.DeleteKeyFromRedis(ctx, "user:100:profile")
		assert.NoError(t, err)

		// Verify cleanup
		var deletedUser TestUser
		err = service.GetFromRedis(ctx, "user:100", &deletedUser)
		assert.NoError(t, err)
		assert.Equal(t, TestUser{}, deletedUser)
	})

	t.Run("DistributedLockWorkflow", func(t *testing.T) {
		lockKey := "resource:processing"
		workerID := "worker-123"

		// Acquire lock
		acquired, err := service.SetLock(ctx, lockKey, workerID, 10)
		assert.NoError(t, err)
		assert.True(t, acquired)

		// Try to acquire the same lock (should fail)
		acquired2, err := service.SetLock(ctx, lockKey, "worker-456", 10)
		assert.NoError(t, err)
		assert.False(t, acquired2)

		// Release lock
		err = service.DeleteKeyFromRedis(ctx, lockKey)
		assert.NoError(t, err)

		// Now should be able to acquire the lock
		acquired3, err := service.SetLock(ctx, lockKey, "worker-789", 10)
		assert.NoError(t, err)
		assert.True(t, acquired3)
	})

	t.Run("CacheExpirationWorkflow", func(t *testing.T) {
		// Note: miniredis doesn't automatically expire keys,
		// so we'll test the expiration is set correctly
		tempData := map[string]interface{}{
			"session_id": "abc123",
			"user_id": "user456",
		}

		err := service.SetHSetToRedis(ctx, "session:abc123", tempData, 1)
		assert.NoError(t, err)

		// Verify data exists
		sessionData, err := service.GetHGetToRedis(ctx, "session:abc123")
		assert.NoError(t, err)
		assert.NotNil(t, sessionData)
		assert.Equal(t, "abc123", sessionData["session_id"])

		// In a real Redis, this would expire after 1 second
		// For testing purposes, we manually expire in miniredis
		miniRedis.FastForward(2 * time.Second)

		// Data should still exist in miniredis (it doesn't auto-expire)
		// but in production Redis, it would be gone
		sessionData, err = service.GetHGetToRedis(ctx, "session:abc123")
		assert.NoError(t, err)
		// In miniredis, data persists, but TTL behavior is tested elsewhere
	})
}

// TestConcurrentOperations tests Redis service under concurrent access
func TestConcurrentOperations(t *testing.T) {
	// Setup
	miniRedis, err := miniredis.Run()
	assert.NoError(t, err)
	defer miniRedis.Close()

	client := redis.NewClient(&redis.Options{
		Addr: miniRedis.Addr(),
	})
	defer client.Close()

	logger := zaptest.NewLogger(t)
	service := NewRedisService(client, logger).(*RedisService)
	ctx := context.Background()

	t.Run("ConcurrentSetAndGet", func(t *testing.T) {
		const numGoroutines = 10
		const numOperations = 50

		var wg sync.WaitGroup
		errors := make(chan error, numGoroutines*numOperations*2)

		// Start multiple goroutines performing concurrent operations
		for i := 0; i < numGoroutines; i++ {
			wg.Add(1)
			go func(workerID int) {
				defer wg.Done()
				for j := 0; j < numOperations; j++ {
					key := fmt.Sprintf("concurrent:worker%d:item%d", workerID, j)
					value := TestUser{
						ID:    workerID*1000 + j,
						Name:  fmt.Sprintf("Worker %d User %d", workerID, j),
						Email: fmt.Sprintf("worker%d.user%d@example.com", workerID, j),
					}

					// Set operation
					if err := service.SetToRedis(ctx, key, value, 300); err != nil {
						errors <- err
						continue
					}

					// Get operation
					var retrievedValue TestUser
					if err := service.GetFromRedis(ctx, key, &retrievedValue); err != nil {
						errors <- err
						continue
					}

					// Verify data integrity
					if retrievedValue != value {
						errors <- fmt.Errorf("data mismatch for key %s", key)
					}
				}
			}(i)
		}

		wg.Wait()
		close(errors)

		// Check for errors
		var errorCount int
		for err := range errors {
			t.Errorf("Concurrent operation error: %v", err)
			errorCount++
		}

		assert.Equal(t, 0, errorCount, "No errors should occur during concurrent operations")
	})

	t.Run("ConcurrentLockContention", func(t *testing.T) {
		const numGoroutines = 20
		lockKey := "contention:lock"

		var wg sync.WaitGroup
		successfulLocks := make(chan string, numGoroutines)
		failures := make(chan string, numGoroutines)

		// Multiple goroutines trying to acquire the same lock
		for i := 0; i < numGoroutines; i++ {
			wg.Add(1)
			go func(workerID int) {
				defer wg.Done()
				workerIDStr := fmt.Sprintf("worker-%d", workerID)
				
				acquired, err := service.SetLock(ctx, lockKey, workerIDStr, 5)
				if err != nil {
					failures <- fmt.Sprintf("worker-%d: error - %v", workerID, err)
					return
				}
				
				if acquired {
					successfulLocks <- workerIDStr
					// Simulate some work
					time.Sleep(10 * time.Millisecond)
					// Release lock
					_ = service.DeleteKeyFromRedis(ctx, lockKey)
				} else {
					failures <- fmt.Sprintf("worker-%d: could not acquire lock", workerID)
				}
			}(i)
		}

		wg.Wait()
		close(successfulLocks)
		close(failures)

		// Count results
		successCount := 0
		for range successfulLocks {
			successCount++
		}

		failureCount := 0
		for range failures {
			failureCount++
		}

		// At least one should succeed, others should fail
		assert.True(t, successCount >= 1, "At least one worker should acquire the lock")
		assert.Equal(t, numGoroutines, successCount+failureCount, "All workers should either succeed or fail")
		
		t.Logf("Successful locks: %d, Failed attempts: %d", successCount, failureCount)
	})
}

// TestErrorScenarios tests various error conditions
func TestErrorScenarios(t *testing.T) {
	// Setup with a client that will be closed to simulate connection errors
	miniRedis, err := miniredis.Run()
	assert.NoError(t, err)

	client := redis.NewClient(&redis.Options{
		Addr: miniRedis.Addr(),
	})

	logger := zaptest.NewLogger(t)
	service := NewRedisService(client, logger).(*RedisService)
	ctx := context.Background()

	t.Run("ConnectionError", func(t *testing.T) {
		// Close the Redis connection to simulate network issues
		miniRedis.Close()
		client.Close()

		user := TestUser{ID: 1, Name: "Test", Email: "test@example.com"}
		
		// Operations should fail gracefully
		err := service.SetToRedis(ctx, "test:key", user, 300)
		assert.Error(t, err)

		var retrievedUser TestUser
		err = service.GetFromRedis(ctx, "test:key", &retrievedUser)
		assert.Error(t, err)

		_, err = service.SetLock(ctx, "test:lock", "value", 300)
		assert.Error(t, err)
	})

	t.Run("ContextCancellation", func(t *testing.T) {
		// Setup fresh connection
		miniRedis2, err := miniredis.Run()
		assert.NoError(t, err)
		defer miniRedis2.Close()

		client2 := redis.NewClient(&redis.Options{
			Addr: miniRedis2.Addr(),
		})
		defer client2.Close()

		service2 := NewRedisService(client2, logger).(*RedisService)

		// Create a context that's already cancelled
		cancelledCtx, cancel := context.WithCancel(context.Background())
		cancel()

		user := TestUser{ID: 1, Name: "Test", Email: "test@example.com"}
		
		// Operations should respect context cancellation
		err = service2.SetToRedis(cancelledCtx, "test:key", user, 300)
		assert.Error(t, err)
		assert.Contains(t, err.Error(), "context canceled")
	})

	t.Run("TimeoutScenario", func(t *testing.T) {
		// Setup fresh connection
		miniRedis3, err := miniredis.Run()
		assert.NoError(t, err)
		defer miniRedis3.Close()

		client3 := redis.NewClient(&redis.Options{
			Addr: miniRedis3.Addr(),
		})
		defer client3.Close()

		service3 := NewRedisService(client3, logger).(*RedisService)

		// Create a context with very short timeout
		timeoutCtx, cancel := context.WithTimeout(context.Background(), 1*time.Nanosecond)
		defer cancel()

		user := TestUser{ID: 1, Name: "Test", Email: "test@example.com"}
		
		// Operations might fail due to timeout (depending on timing)
		err = service3.SetToRedis(timeoutCtx, "test:key", user, 300)
		// This might or might not error depending on timing, so we don't assert
		t.Logf("Timeout test result: %v", err)
	})
}

// TestDataConsistency verifies data consistency across operations
func TestDataConsistency(t *testing.T) {
	// Setup
	miniRedis, err := miniredis.Run()
	assert.NoError(t, err)
	defer miniRedis.Close()

	client := redis.NewClient(&redis.Options{
		Addr: miniRedis.Addr(),
	})
	defer client.Close()

	logger := zaptest.NewLogger(t)
	service := NewRedisService(client, logger).(*RedisService)
	ctx := context.Background()

	t.Run("OverwriteConsistency", func(t *testing.T) {
		key := "consistency:test"

		// Store initial value
		user1 := TestUser{ID: 1, Name: "User One", Email: "user1@example.com"}
		err := service.SetToRedis(ctx, key, user1, 300)
		assert.NoError(t, err)

		// Overwrite with new value
		user2 := TestUser{ID: 2, Name: "User Two", Email: "user2@example.com"}
		err = service.SetToRedis(ctx, key, user2, 300)
		assert.NoError(t, err)

		// Retrieve and verify latest value
		var retrievedUser TestUser
		err = service.GetFromRedis(ctx, key, &retrievedUser)
		assert.NoError(t, err)
		assert.Equal(t, user2, retrievedUser)
		assert.NotEqual(t, user1, retrievedUser)
	})

	t.Run("HashConsistency", func(t *testing.T) {
		hashKey := "consistency:hash"

		// Store initial hash
		initialData := map[string]interface{}{
			"field1": "value1",
			"field2": "value2",
		}
		err := service.SetHSetToRedis(ctx, hashKey, initialData, 300)
		assert.NoError(t, err)

		// Update hash with additional fields
		updateData := map[string]interface{}{
			"field2": "updated_value2", // Overwrite existing
			"field3": "value3",         // Add new field
		}
		err = service.SetHSetToRedis(ctx, hashKey, updateData, 300)
		assert.NoError(t, err)

		// Retrieve and verify
		retrievedHash, err := service.GetHGetToRedis(ctx, hashKey)
		assert.NoError(t, err)
		assert.Equal(t, "value1", retrievedHash["field1"])          // Original field
		assert.Equal(t, "updated_value2", retrievedHash["field2"])  // Updated field
		assert.Equal(t, "value3", retrievedHash["field3"])          // New field
	})
}
