package service

import (
	"context"
	"encoding/json"
	"testing"
	"time"

	"github.com/alicebob/miniredis/v2"
	"github.com/redis/go-redis/v9"
	"github.com/stretchr/testify/suite"
	"go.uber.org/zap"
	"go.uber.org/zap/zaptest"
)

// RedisServiceTestSuite provides a test suite for RedisService
type RedisServiceTestSuite struct {
	suite.Suite
	redisService *RedisService
	redisClient  *redis.Client
	miniRedis    *miniredis.Miniredis
	logger       *zap.Logger
	ctx          context.Context
}

// SetupSuite initializes the test suite with a mock Redis server
func (suite *RedisServiceTestSuite) SetupSuite() {
	// Create a new miniredis instance for testing
	var err error
	suite.miniRedis, err = miniredis.Run()
	suite.Require().NoError(err)

	// Create Redis client connected to miniredis
	suite.redisClient = redis.NewClient(&redis.Options{
		Addr: suite.miniRedis.Addr(),
	})

	// Create test logger
	suite.logger = zaptest.NewLogger(suite.T())

	// Create RedisService instance
	service := NewRedisService(suite.redisClient, suite.logger)
	suite.redisService = service.(*RedisService)

	// Create context for tests
	suite.ctx = context.Background()
}

// TearDownSuite cleans up the test suite
func (suite *RedisServiceTestSuite) TearDownSuite() {
	suite.redisClient.Close()
	suite.miniRedis.Close()
}

// SetupTest runs before each test to ensure clean state
func (suite *RedisServiceTestSuite) SetupTest() {
	suite.miniRedis.FlushAll()
}

// TestUser represents a test user structure
type TestUser struct {
	ID    int    `json:"id"`
	Name  string `json:"name"`
	Email string `json:"email"`
}

// TestSetToRedis_Success tests successful value storage
func (suite *RedisServiceTestSuite) TestSetToRedis_Success() {
	user := TestUser{
		ID:    123,
		Name:  "John Doe",
		Email: "john@example.com",
	}

	err := suite.redisService.SetToRedis(suite.ctx, "user:123", user, 3600)
	suite.NoError(err)

	// Verify the value was stored
	val, err := suite.miniRedis.Get("user:123")
	suite.NoError(err)
	suite.NotEmpty(val)

	var storedUser TestUser
	err = json.Unmarshal([]byte(val), &storedUser)
	suite.NoError(err)
	suite.Equal(user, storedUser)
}

// TestSetToRedis_WithZeroExpiration tests storage without expiration
func (suite *RedisServiceTestSuite) TestSetToRedis_WithZeroExpiration() {
	user := TestUser{ID: 456, Name: "Jane Doe", Email: "jane@example.com"}

	err := suite.redisService.SetToRedis(suite.ctx, "user:456", user, 0)
	suite.NoError(err)

	// Check that no TTL is set
	ttl := suite.miniRedis.TTL("user:456")
	suite.Equal(time.Duration(0), ttl)
}

// TestSetToRedis_MarshalError tests handling of marshal errors
func (suite *RedisServiceTestSuite) TestSetToRedis_MarshalError() {
	// Use a channel which cannot be marshaled to JSON
	invalidValue := make(chan int)

	err := suite.redisService.SetToRedis(suite.ctx, "invalid", invalidValue, 3600)
	suite.Error(err)
}

// TestGetFromRedis_Success tests successful value retrieval
func (suite *RedisServiceTestSuite) TestGetFromRedis_Success() {
	user := TestUser{
		ID:    789,
		Name:  "Bob Smith",
		Email: "bob@example.com",
	}

	// Store the value first
	data, _ := json.Marshal(user)
	suite.miniRedis.Set("user:789", string(data))

	// Retrieve the value
	var retrievedUser TestUser
	err := suite.redisService.GetFromRedis(suite.ctx, "user:789", &retrievedUser)
	suite.NoError(err)
	suite.Equal(user, retrievedUser)
}

// TestGetFromRedis_KeyNotFound tests handling of non-existent keys
func (suite *RedisServiceTestSuite) TestGetFromRedis_KeyNotFound() {
	var user TestUser
	err := suite.redisService.GetFromRedis(suite.ctx, "nonexistent", &user)
	suite.NoError(err) // Should return nil for non-existent keys
	suite.Equal(TestUser{}, user) // Should remain zero value
}

// TestGetFromRedis_UnmarshalError tests handling of unmarshal errors
func (suite *RedisServiceTestSuite) TestGetFromRedis_UnmarshalError() {
	// Store invalid JSON
	suite.miniRedis.Set("invalid", "invalid json data")

	var user TestUser
	err := suite.redisService.GetFromRedis(suite.ctx, "invalid", &user)
	suite.Error(err)
}

// TestDeleteKeyFromRedis_Success tests successful key deletion
func (suite *RedisServiceTestSuite) TestDeleteKeyFromRedis_Success() {
	// Store a value first
	suite.miniRedis.Set("delete:me", "test value")
	suite.True(suite.miniRedis.Exists("delete:me"))

	// Delete the key
	err := suite.redisService.DeleteKeyFromRedis(suite.ctx, "delete:me")
	suite.NoError(err)

	// Verify deletion
	suite.False(suite.miniRedis.Exists("delete:me"))
}

// TestDeleteKeyFromRedis_NonExistentKey tests deletion of non-existent keys
func (suite *RedisServiceTestSuite) TestDeleteKeyFromRedis_NonExistentKey() {
	err := suite.redisService.DeleteKeyFromRedis(suite.ctx, "nonexistent")
	suite.NoError(err) // Redis DEL command doesn't error for non-existent keys
}

// TestSetHSetToRedis_Success tests successful hash storage
func (suite *RedisServiceTestSuite) TestSetHSetToRedis_Success() {
	hashData := map[string]interface{}{
		"name":  "John Doe",
		"age":   "30",
		"email": "john@example.com",
	}

	err := suite.redisService.SetHSetToRedis(suite.ctx, "user:hash:123", hashData, 3600)
	suite.NoError(err)

	// Verify the hash was stored
	storedName := suite.miniRedis.HGet("user:hash:123", "name")
	storedAge := suite.miniRedis.HGet("user:hash:123", "age") 
	storedEmail := suite.miniRedis.HGet("user:hash:123", "email")
	suite.Equal("John Doe", storedName)
	suite.Equal("30", storedAge)
	suite.Equal("john@example.com", storedEmail)

	// Verify expiration was set
	ttl := suite.miniRedis.TTL("user:hash:123")
	suite.True(ttl > 0)
}

// TestSetHSetToRedis_WithZeroExpiration tests hash storage without expiration
func (suite *RedisServiceTestSuite) TestSetHSetToRedis_WithZeroExpiration() {
	hashData := map[string]interface{}{
		"name": "Jane Doe",
		"age":  "25",
	}

	err := suite.redisService.SetHSetToRedis(suite.ctx, "user:hash:456", hashData, 0)
	suite.NoError(err)

	// Check that no TTL is set
	ttl := suite.miniRedis.TTL("user:hash:456")
	suite.Equal(time.Duration(0), ttl)
}

// TestGetHGetToRedis_Success tests successful hash retrieval
func (suite *RedisServiceTestSuite) TestGetHGetToRedis_Success() {
	// Store hash data first
	suite.miniRedis.HSet("user:hash:789", "name", "Bob Smith")
	suite.miniRedis.HSet("user:hash:789", "age", "35")
	suite.miniRedis.HSet("user:hash:789", "email", "bob@example.com")

	// Retrieve the hash
	result, err := suite.redisService.GetHGetToRedis(suite.ctx, "user:hash:789")
	suite.NoError(err)
	suite.NotNil(result)
	suite.Equal("Bob Smith", result["name"])
	suite.Equal("35", result["age"])
	suite.Equal("bob@example.com", result["email"])
}

// TestGetHGetToRedis_NonExistentHash tests retrieval of non-existent hash
func (suite *RedisServiceTestSuite) TestGetHGetToRedis_NonExistentHash() {
	result, err := suite.redisService.GetHGetToRedis(suite.ctx, "nonexistent:hash")
	suite.NoError(err)
	suite.NotNil(result) // miniredis returns empty map for non-existent hash
	suite.Empty(result)
}

// TestSetLock_Success tests successful lock acquisition
func (suite *RedisServiceTestSuite) TestSetLock_Success() {
	lockKey := "process:lock"
	lockValue := "unique-lock-id-123"

	acquired, err := suite.redisService.SetLock(suite.ctx, lockKey, lockValue, 300)
	suite.NoError(err)
	suite.True(acquired)

	// Verify the lock was set
	storedValue, err := suite.miniRedis.Get(lockKey)
	suite.NoError(err)
	suite.Equal(lockValue, storedValue)

	// Verify expiration was set
	ttl := suite.miniRedis.TTL(lockKey)
	suite.True(ttl > 0)
}

// TestSetLock_AlreadyExists tests lock acquisition when lock already exists
func (suite *RedisServiceTestSuite) TestSetLock_AlreadyExists() {
	lockKey := "process:lock"
	firstLockValue := "first-lock-id"
	secondLockValue := "second-lock-id"

	// Set initial lock
	suite.miniRedis.Set(lockKey, firstLockValue)

	// Try to acquire the same lock
	acquired, err := suite.redisService.SetLock(suite.ctx, lockKey, secondLockValue, 300)
	suite.NoError(err)
	suite.False(acquired)

	// Verify original lock is still there
	storedValue, err := suite.miniRedis.Get(lockKey)
	suite.NoError(err)
	suite.Equal(firstLockValue, storedValue)
}

// TestSetLock_WithZeroExpiration tests lock with zero expiration
func (suite *RedisServiceTestSuite) TestSetLock_WithZeroExpiration() {
	lockKey := "process:lock:persistent"
	lockValue := "persistent-lock-id"

	acquired, err := suite.redisService.SetLock(suite.ctx, lockKey, lockValue, 0)
	suite.NoError(err)
	suite.True(acquired)

	// Check that no TTL is set (persistent lock)
	ttl := suite.miniRedis.TTL(lockKey)
	suite.Equal(time.Duration(0), ttl)
}

// TestNewRedisService tests the constructor
func (suite *RedisServiceTestSuite) TestNewRedisService() {
	service := NewRedisService(suite.redisClient, suite.logger)
	suite.NotNil(service)

	// Verify it implements the interface
	redisService, ok := service.(*RedisService)
	suite.True(ok)
	suite.Equal(suite.redisClient, redisService.redisClient)
	suite.Equal(suite.logger, redisService.logger)
}

// TestRedisServiceTestSuite runs the test suite
func TestRedisServiceTestSuite(t *testing.T) {
	suite.Run(t, new(RedisServiceTestSuite))
}

// Benchmark tests for performance evaluation

// BenchmarkSetToRedis benchmarks the SetToRedis method
func BenchmarkSetToRedis(b *testing.B) {
	miniRedis, _ := miniredis.Run()
	defer miniRedis.Close()

	client := redis.NewClient(&redis.Options{
		Addr: miniRedis.Addr(),
	})
	defer client.Close()

	logger := zap.NewNop()
	service := NewRedisService(client, logger).(*RedisService)
	ctx := context.Background()

	user := TestUser{
		ID:    123,
		Name:  "Benchmark User",
		Email: "benchmark@example.com",
	}

	b.ResetTimer()
	for i := 0; i < b.N; i++ {
		_ = service.SetToRedis(ctx, "benchmark:user", user, 3600)
	}
}

// BenchmarkGetFromRedis benchmarks the GetFromRedis method
func BenchmarkGetFromRedis(b *testing.B) {
	miniRedis, _ := miniredis.Run()
	defer miniRedis.Close()

	client := redis.NewClient(&redis.Options{
		Addr: miniRedis.Addr(),
	})
	defer client.Close()

	logger := zap.NewNop()
	service := NewRedisService(client, logger).(*RedisService)
	ctx := context.Background()

	// Pre-populate data
	user := TestUser{
		ID:    123,
		Name:  "Benchmark User",
		Email: "benchmark@example.com",
	}
	_ = service.SetToRedis(ctx, "benchmark:user", user, 3600)

	b.ResetTimer()
	for i := 0; i < b.N; i++ {
		var retrievedUser TestUser
		_ = service.GetFromRedis(ctx, "benchmark:user", &retrievedUser)
	}
}

// BenchmarkSetLock benchmarks the SetLock method
func BenchmarkSetLock(b *testing.B) {
	miniRedis, _ := miniredis.Run()
	defer miniRedis.Close()

	client := redis.NewClient(&redis.Options{
		Addr: miniRedis.Addr(),
	})
	defer client.Close()

	logger := zap.NewNop()
	service := NewRedisService(client, logger).(*RedisService)
	ctx := context.Background()

	b.ResetTimer()
	for i := 0; i < b.N; i++ {
		lockKey := "benchmark:lock:" + string(rune(i))
		_, _ = service.SetLock(ctx, lockKey, "value", 300)
	}
}
