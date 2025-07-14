package service

import (
	"context"
	"fmt"
	"testing"

	"github.com/alicebob/miniredis/v2"
	"github.com/redis/go-redis/v9"
	"go.uber.org/zap/zaptest"
)

// SimpleTestUser represents a simple user structure for testing (renamed to avoid conflict)
type SimpleTestUser struct {
	ID    int    `json:"id"`
	Name  string `json:"name"`
	Email string `json:"email"`
}

// TestRedisServiceBasic provides basic tests for RedisService without external dependencies
func TestRedisServiceBasic(t *testing.T) {
	// Setup miniredis
	miniRedis, err := miniredis.Run()
	if err != nil {
		t.Fatalf("Failed to start miniredis: %v", err)
	}
	defer miniRedis.Close()

	// Create Redis client
	client := redis.NewClient(&redis.Options{
		Addr: miniRedis.Addr(),
	})
	defer client.Close()

	// Create logger
	logger := zaptest.NewLogger(t)

	// Create Redis service
	service := NewRedisService(client, logger)
	ctx := context.Background()

	t.Run("SetAndGetValue", func(t *testing.T) {
		user := SimpleTestUser{
			ID:    123,
			Name:  "Test User",
			Email: "test@example.com",
		}

		// Test SetToRedis
		err := service.SetToRedis(ctx, "user:123", user, 3600)
		if err != nil {
			t.Errorf("SetToRedis failed: %v", err)
		}

		// Test GetFromRedis
		var retrievedUser SimpleTestUser
		err = service.GetFromRedis(ctx, "user:123", &retrievedUser)
		if err != nil {
			t.Errorf("GetFromRedis failed: %v", err)
		}

		// Verify data
		if retrievedUser.ID != user.ID {
			t.Errorf("Expected ID %d, got %d", user.ID, retrievedUser.ID)
		}
		if retrievedUser.Name != user.Name {
			t.Errorf("Expected Name %s, got %s", user.Name, retrievedUser.Name)
		}
		if retrievedUser.Email != user.Email {
			t.Errorf("Expected Email %s, got %s", user.Email, retrievedUser.Email)
		}
	})

	t.Run("GetNonExistentKey", func(t *testing.T) {
		var user SimpleTestUser
		err := service.GetFromRedis(ctx, "nonexistent", &user)
		if err != nil {
			t.Errorf("GetFromRedis should not error for non-existent key: %v", err)
		}

		// Should be zero value
		if user.ID != 0 || user.Name != "" || user.Email != "" {
			t.Errorf("Expected zero value, got %+v", user)
		}
	})

	t.Run("DeleteKey", func(t *testing.T) {
		// Set a value first
		err := service.SetToRedis(ctx, "delete:me", "test value", 300)
		if err != nil {
			t.Errorf("SetToRedis failed: %v", err)
		}

		// Delete it
		err = service.DeleteKeyFromRedis(ctx, "delete:me")
		if err != nil {
			t.Errorf("DeleteKeyFromRedis failed: %v", err)
		}

		// Verify it's gone
		var result string
		err = service.GetFromRedis(ctx, "delete:me", &result)
		if err != nil {
			t.Errorf("GetFromRedis failed: %v", err)
		}
		if result != "" {
			t.Errorf("Expected empty result after deletion, got %s", result)
		}
	})

	t.Run("HashOperations", func(t *testing.T) {
		hashData := map[string]interface{}{
			"name":  "John Doe",
			"age":   30,
			"email": "john@example.com",
		}

		// Set hash
		err := service.SetHSetToRedis(ctx, "user:hash:123", hashData, 3600)
		if err != nil {
			t.Errorf("SetHSetToRedis failed: %v", err)
		}

		// Get hash
		result, err := service.GetHGetToRedis(ctx, "user:hash:123")
		if err != nil {
			t.Errorf("GetHGetToRedis failed: %v", err)
		}

		if result == nil {
			t.Errorf("Expected hash result, got nil")
			return
		}

		// Verify hash data
		if result["name"] != "John Doe" {
			t.Errorf("Expected name 'John Doe', got %s", result["name"])
		}
		if result["age"] != "30" {
			t.Errorf("Expected age '30', got %s", result["age"])
		}
		if result["email"] != "john@example.com" {
			t.Errorf("Expected email 'john@example.com', got %s", result["email"])
		}
	})

	t.Run("LockOperations", func(t *testing.T) {
		lockKey := "test:lock"
		lockValue := "test-lock-id"

		// Acquire lock
		acquired, err := service.SetLock(ctx, lockKey, lockValue, 300)
		if err != nil {
			t.Errorf("SetLock failed: %v", err)
		}
		if !acquired {
			t.Errorf("Expected to acquire lock")
		}

		// Try to acquire same lock (should fail)
		acquired2, err := service.SetLock(ctx, lockKey, "different-id", 300)
		if err != nil {
			t.Errorf("Second SetLock failed: %v", err)
		}
		if acquired2 {
			t.Errorf("Expected second lock acquisition to fail")
		}

		// Release lock
		err = service.DeleteKeyFromRedis(ctx, lockKey)
		if err != nil {
			t.Errorf("DeleteKeyFromRedis failed: %v", err)
		}

		// Now should be able to acquire
		acquired3, err := service.SetLock(ctx, lockKey, "new-id", 300)
		if err != nil {
			t.Errorf("Third SetLock failed: %v", err)
		}
		if !acquired3 {
			t.Errorf("Expected to acquire lock after release")
		}
	})

	t.Run("InvalidJSON", func(t *testing.T) {
		// Try to marshal invalid data
		invalidData := make(chan int) // channels cannot be marshaled to JSON
		err := service.SetToRedis(ctx, "invalid", invalidData, 300)
		if err == nil {
			t.Errorf("Expected error when marshaling invalid data")
		}
	})
}

// TestRedisServiceConcurrency tests concurrent operations
func TestRedisServiceConcurrency(t *testing.T) {
	// Setup
	miniRedis, err := miniredis.Run()
	if err != nil {
		t.Fatalf("Failed to start miniredis: %v", err)
	}
	defer miniRedis.Close()

	client := redis.NewClient(&redis.Options{
		Addr: miniRedis.Addr(),
	})
	defer client.Close()

	logger := zaptest.NewLogger(t)
	service := NewRedisService(client, logger)
	ctx := context.Background()

	t.Run("ConcurrentSetGet", func(t *testing.T) {
		const numGoroutines = 10
		const numOperations = 20

		errChan := make(chan error, numGoroutines*numOperations*2)
		done := make(chan bool, numGoroutines)

		for i := 0; i < numGoroutines; i++ {
			go func(workerID int) {
				defer func() { done <- true }()
				
				for j := 0; j < numOperations; j++ {
					key := fmt.Sprintf("concurrent:worker%d:item%d", workerID, j)
					user := SimpleTestUser{
						ID:    workerID*1000 + j,
						Name:  fmt.Sprintf("Worker %d User %d", workerID, j),
						Email: fmt.Sprintf("worker%d@example.com", workerID),
					}

					// Set
					if err := service.SetToRedis(ctx, key, user, 300); err != nil {
						errChan <- err
						continue
					}

					// Get
					var retrieved SimpleTestUser
					if err := service.GetFromRedis(ctx, key, &retrieved); err != nil {
						errChan <- err
						continue
					}

					// Verify
					if retrieved.ID != user.ID {
						errChan <- fmt.Errorf("data mismatch for %s: expected ID %d, got %d", key, user.ID, retrieved.ID)
					}
				}
			}(i)
		}

		// Wait for completion
		for i := 0; i < numGoroutines; i++ {
			<-done
		}
		close(errChan)

		// Check for errors
		errorCount := 0
		for err := range errChan {
			t.Errorf("Concurrent operation error: %v", err)
			errorCount++
		}

		if errorCount > 0 {
			t.Errorf("Got %d errors during concurrent operations", errorCount)
		}
	})
}

// BenchmarkRedisOperations provides performance benchmarks
func BenchmarkRedisOperations(b *testing.B) {
	// Setup
	miniRedis, err := miniredis.Run()
	if err != nil {
		b.Fatalf("Failed to start miniredis: %v", err)
	}
	defer miniRedis.Close()

	client := redis.NewClient(&redis.Options{
		Addr: miniRedis.Addr(),
	})
	defer client.Close()

	logger := zaptest.NewLogger(b)
	service := NewRedisService(client, logger)
	ctx := context.Background()

	user := SimpleTestUser{
		ID:    123,
		Name:  "Benchmark User",
		Email: "benchmark@example.com",
	}

	b.Run("SetToRedis", func(b *testing.B) {
		b.ResetTimer()
		for i := 0; i < b.N; i++ {
			_ = service.SetToRedis(ctx, "benchmark:set", user, 3600)
		}
	})

	// Pre-populate for get benchmark
	_ = service.SetToRedis(ctx, "benchmark:get", user, 3600)

	b.Run("GetFromRedis", func(b *testing.B) {
		b.ResetTimer()
		for i := 0; i < b.N; i++ {
			var retrieved SimpleTestUser
			_ = service.GetFromRedis(ctx, "benchmark:get", &retrieved)
		}
	})

	b.Run("SetLock", func(b *testing.B) {
		b.ResetTimer()
		for i := 0; i < b.N; i++ {
			lockKey := fmt.Sprintf("benchmark:lock:%d", i)
			_, _ = service.SetLock(ctx, lockKey, "value", 300)
		}
	})
}
