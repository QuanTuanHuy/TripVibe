# Redis Service Documentation

## Tổng quan

`RedisService` là một implementation của interface `IRedisPort` cung cấp các operations cho Redis. Service này bao gồm các tính năng chính:

- Lưu trữ và truy xuất key-value với JSON serialization
- Operations cho Redis hash 
- Distributed locking với SETNX
- Hỗ trợ expiration cho tất cả operations
- Comprehensive logging với zap

## Architecture

```
├── core/port/redis_port.go          # Interface definition
└── infrastructure/service/
    ├── redis.go                     # Implementation
    ├── redis_test.go               # Unit tests với testify
    ├── redis_integration_test.go   # Integration tests
    └── redis_simple_test.go        # Basic tests không dependencies
```

## Interface Definition

```go
type IRedisPort interface {
    SetToRedis(ctx context.Context, key string, value interface{}, expiration int64) error
    GetFromRedis(ctx context.Context, key string, destination interface{}) error
    SetHSetToRedis(ctx context.Context, key string, mapFieldValue map[string]interface{}, expiration int64) error
    GetHGetToRedis(ctx context.Context, key string) (map[string]string, error)
    DeleteKeyFromRedis(ctx context.Context, key string) error
    SetLock(ctx context.Context, key string, value string, expiration int64) (bool, error)
}
```

## Usage Examples

### 1. Basic Key-Value Operations

```go
// Initialize service
client := redis.NewClient(&redis.Options{
    Addr: "localhost:6379",
})
logger := zap.NewProduction()
redisService := NewRedisService(client, logger)

// Store user data
user := User{
    ID:    123,
    Name:  "John Doe",
    Email: "john@example.com",
}

ctx := context.Background()
err := redisService.SetToRedis(ctx, "user:123", user, 3600) // 1 hour expiration
if err != nil {
    log.Printf("Failed to store user: %v", err)
}

// Retrieve user data
var retrievedUser User
err = redisService.GetFromRedis(ctx, "user:123", &retrievedUser)
if err != nil {
    log.Printf("Failed to retrieve user: %v", err)
}
```

### 2. Hash Operations

```go
// Store user profile as hash
profile := map[string]interface{}{
    "last_login": time.Now().Format(time.RFC3339),
    "login_count": 5,
    "status": "active",
    "preferences": `{"theme": "dark", "lang": "en"}`,
}

err := redisService.SetHSetToRedis(ctx, "user:123:profile", profile, 7200) // 2 hours
if err != nil {
    log.Printf("Failed to store profile: %v", err)
}

// Retrieve profile hash
profileData, err := redisService.GetHGetToRedis(ctx, "user:123:profile")
if err != nil {
    log.Printf("Failed to retrieve profile: %v", err)
}

if profileData != nil {
    loginCount := profileData["login_count"]
    status := profileData["status"]
    // Use the data...
}
```

### 3. Distributed Locking

```go
// Acquire distributed lock
lockKey := "process:critical_section"
workerID := uuid.New().String()
lockDuration := int64(300) // 5 minutes

acquired, err := redisService.SetLock(ctx, lockKey, workerID, lockDuration)
if err != nil {
    log.Printf("Lock operation failed: %v", err)
    return
}

if !acquired {
    log.Println("Could not acquire lock, another process is running")
    return
}

// Critical section - only one process executes this
defer func() {
    // Release lock when done
    if err := redisService.DeleteKeyFromRedis(ctx, lockKey); err != nil {
        log.Printf("Failed to release lock: %v", err)
    }
}()

// Do critical work here...
log.Println("Executing critical section")
```

### 4. Cache Pattern với Error Handling

```go
func GetUserWithCache(ctx context.Context, userID int, redisService port.IRedisPort, userRepo UserRepository) (*User, error) {
    cacheKey := fmt.Sprintf("user:%d", userID)
    
    // Try cache first
    var cachedUser User
    err := redisService.GetFromRedis(ctx, cacheKey, &cachedUser)
    if err != nil {
        log.Printf("Redis error, falling back to database: %v", err)
    } else if cachedUser.ID != 0 {
        // Cache hit
        return &cachedUser, nil
    }
    
    // Cache miss - get from database
    user, err := userRepo.GetByID(ctx, userID)
    if err != nil {
        return nil, err
    }
    
    // Store in cache (fire and forget)
    go func() {
        if err := redisService.SetToRedis(context.Background(), cacheKey, user, 3600); err != nil {
            log.Printf("Failed to cache user: %v", err)
        }
    }()
    
    return user, nil
}
```

## Configuration

### Redis Client Setup

```go
import (
    "github.com/redis/go-redis/v9"
    "go.uber.org/zap"
)

func SetupRedisService() port.IRedisPort {
    // Basic configuration
    client := redis.NewClient(&redis.Options{
        Addr:     "localhost:6379",
        Password: "", // no password
        DB:       0,  // default DB
    })
    
    // Production configuration
    client = redis.NewClient(&redis.Options{
        Addr:         "redis.example.com:6379",
        Password:     os.Getenv("REDIS_PASSWORD"),
        DB:           0,
        PoolSize:     10,
        MinIdleConns: 5,
        MaxRetries:   3,
        DialTimeout:  5 * time.Second,
        ReadTimeout:  3 * time.Second,
        WriteTimeout: 3 * time.Second,
    })
    
    // Logger setup
    logger, _ := zap.NewProduction()
    
    return NewRedisService(client, logger)
}
```

### Dependency Injection với Fx

```go
import "go.uber.org/fx"

var RedisModule = fx.Module("redis",
    fx.Provide(
        NewRedisClient,
        NewRedisService,
    ),
)

func NewRedisClient() *redis.Client {
    return redis.NewClient(&redis.Options{
        Addr: "localhost:6379",
    })
}
```

## Error Handling

Service xử lý các lỗi phổ biến:

### 1. Connection Errors
```go
// Service sẽ log lỗi và return error để caller xử lý
err := redisService.SetToRedis(ctx, "key", "value", 300)
if err != nil {
    // Implement fallback logic
    log.Printf("Redis unavailable, using fallback: %v", err)
    return fallbackOperation()
}
```

### 2. Serialization Errors
```go
// JSON marshal/unmarshal errors
type InvalidStruct struct {
    Channel chan int `json:"-"` // Cannot be serialized
}

invalid := InvalidStruct{Channel: make(chan int)}
err := redisService.SetToRedis(ctx, "invalid", invalid, 300)
// Returns: json: unsupported type: chan int
```

### 3. Context Cancellation
```go
ctx, cancel := context.WithTimeout(context.Background(), 100*time.Millisecond)
defer cancel()

// Operations respect context cancellation
err := redisService.SetToRedis(ctx, "key", "value", 300)
if err != nil && err == context.DeadlineExceeded {
    log.Println("Operation timed out")
}
```

## Best Practices

### 1. Key Naming Convention
```go
// Use consistent, hierarchical naming
const (
    UserCacheKey     = "user:%d"           // user:123
    UserProfileKey   = "user:%d:profile"   // user:123:profile
    UserSessionKey   = "session:%s"        // session:abc123
    ProcessLockKey   = "lock:process:%s"   // lock:process:email_sender
)
```

### 2. Expiration Strategy
```go
const (
    ShortCache  = 300   // 5 minutes - frequently changing data
    MediumCache = 3600  // 1 hour - user sessions
    LongCache   = 86400 // 24 hours - static data
    LockTimeout = 300   // 5 minutes - distributed locks
)
```

### 3. Graceful Degradation
```go
func GetDataWithFallback(ctx context.Context, key string, redisService port.IRedisPort, dbService DataService) (*Data, error) {
    var data Data
    
    // Try Redis first
    err := redisService.GetFromRedis(ctx, key, &data)
    if err == nil && data.ID != 0 {
        return &data, nil
    }
    
    // Fallback to database
    dbData, err := dbService.GetData(ctx, key)
    if err != nil {
        return nil, err
    }
    
    // Async cache update
    go func() {
        ctx := context.Background()
        if err := redisService.SetToRedis(ctx, key, dbData, MediumCache); err != nil {
            log.Printf("Failed to update cache: %v", err)
        }
    }()
    
    return dbData, nil
}
```

## Testing

### Unit Tests
```bash
# Run unit tests
cd src/infrastructure/service
go test -v -run TestRedisServiceBasic

# Run with coverage
go test -v -cover -coverprofile=coverage.out
go tool cover -html=coverage.out
```

### Integration Tests
```bash
# Run integration tests (requires real Redis)
go test -v -run TestRedisServiceIntegration

# Run all tests
go test -v ./...
```

### Benchmarks
```bash
# Run performance benchmarks
go test -v -bench=BenchmarkRedis -benchmem

# Results example:
# BenchmarkRedisOperations/SetToRedis-8    10000    150 ns/op    32 B/op    2 allocs/op
# BenchmarkRedisOperations/GetFromRedis-8   8000    180 ns/op    48 B/op    3 allocs/op
```

## Monitoring & Observability

### Logging
Service sử dụng structured logging với zap:

```go
// Success operations
logger.Info("Value set in Redis", 
    zap.String("key", key),
    zap.Int64("expiration", expiration))

// Warnings
logger.Warn("Key not found in Redis", zap.String("key", key))

// Errors
logger.Error("Failed to set value in Redis", 
    zap.String("key", key), 
    zap.Error(err))
```

### Metrics (Optional)
```go
// Add metrics collection
import "github.com/prometheus/client_golang/prometheus"

var (
    redisOperations = prometheus.NewCounterVec(
        prometheus.CounterOpts{
            Name: "redis_operations_total",
            Help: "Total Redis operations",
        },
        []string{"operation", "status"},
    )
    
    redisLatency = prometheus.NewHistogramVec(
        prometheus.HistogramOpts{
            Name: "redis_operation_duration_seconds",
            Help: "Redis operation latency",
        },
        []string{"operation"},
    )
)

// In service methods
func (r *RedisService) SetToRedis(ctx context.Context, key string, value interface{}, expiration int64) error {
    start := time.Now()
    defer func() {
        redisLatency.WithLabelValues("set").Observe(time.Since(start).Seconds())
    }()
    
    // ... implementation
    
    redisOperations.WithLabelValues("set", "success").Inc()
    return nil
}
```

## Performance Considerations

### 1. Connection Pooling
```go
// Configure connection pool
client := redis.NewClient(&redis.Options{
    Addr:         "localhost:6379",
    PoolSize:     50,              // Max connections
    MinIdleConns: 10,              // Minimum idle connections
    MaxRetries:   3,               // Retry failed operations
    PoolTimeout:  30 * time.Second, // Pool timeout
})
```

### 2. Pipeline Operations (Advanced)
```go
// For bulk operations, consider using pipeline
pipe := client.Pipeline()
for i := 0; i < 1000; i++ {
    pipe.Set(ctx, fmt.Sprintf("key:%d", i), fmt.Sprintf("value:%d", i), time.Hour)
}
_, err := pipe.Exec(ctx)
```

### 3. Memory Optimization
```go
// Use appropriate data structures
// For counters: INCR/DECR
// For sets: SADD/SMEMBERS  
// For sorted data: ZADD/ZRANGE
// For time series: Streams or time-based keys
```

## Migration & Maintenance

### Schema Evolution
```go
// Version your cached data
type CacheData struct {
    Version int         `json:"version"`
    Data    interface{} `json:"data"`
}

func (r *RedisService) SetVersionedData(ctx context.Context, key string, data interface{}, version int, expiration int64) error {
    versionedData := CacheData{
        Version: version,
        Data:    data,
    }
    return r.SetToRedis(ctx, key, versionedData, expiration)
}
```

### Cache Warming
```go
func WarmupCache(ctx context.Context, redisService port.IRedisPort, userRepo UserRepository) error {
    // Preload frequently accessed data
    activeUsers, err := userRepo.GetActiveUsers(ctx)
    if err != nil {
        return err
    }
    
    for _, user := range activeUsers {
        key := fmt.Sprintf("user:%d", user.ID)
        if err := redisService.SetToRedis(ctx, key, user, LongCache); err != nil {
            log.Printf("Failed to warm cache for user %d: %v", user.ID, err)
        }
    }
    
    return nil
}
```

## Troubleshooting

### Common Issues

1. **Connection Timeout**
   - Check network connectivity
   - Verify Redis server is running
   - Check firewall rules

2. **Memory Issues**
   - Monitor Redis memory usage
   - Set appropriate expiration times
   - Consider data compression for large objects

3. **Lock Contention**
   - Use shorter lock durations
   - Implement backoff strategies
   - Consider using Redis Streams for queuing

4. **Serialization Errors**
   - Ensure all data types are JSON serializable
   - Handle circular references
   - Use custom marshaling for complex types

### Debug Commands
```bash
# Connect to Redis CLI
redis-cli

# Monitor operations
MONITOR

# Check memory usage
INFO memory

# List all keys (careful in production)
KEYS *

# Check key expiration
TTL user:123
```
