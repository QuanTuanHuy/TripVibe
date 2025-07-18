package async

import (
	"context"
	"fmt"
	"testing"
	"time"

	"github.com/alicebob/miniredis/v2"
	"github.com/redis/go-redis/v9"
	"github.com/stretchr/testify/assert"
	"go.uber.org/zap"

	entity "github.com/quantuanhuy/lib/src/core/entity/async"
)

func setupRedisManager(t *testing.T, ttl time.Duration) (*RedisCorrelationManager, *miniredis.Miniredis) {
	mr, err := miniredis.Run()
	if err != nil {
		t.Fatalf("failed to start miniredis: %v", err)
	}
	client := redis.NewClient(&redis.Options{
		Addr: mr.Addr(),
	})
	logger := zap.NewNop()
	mgr := NewRedisCorrelationManager(client, logger, "prefix", ttl)
	// cast to concrete type if needed
	rm, ok := mgr.(*RedisCorrelationManager)
	if !ok {
		t.Fatalf("unexpected type: %T", mgr)
	}
	return rm, mr
}

func TestStoreAndGetCorrelation(t *testing.T) {
	manager, mr := setupRedisManager(t, 5*time.Second)
	defer mr.Close()

	ctx := context.Background()
	corrID := "corr1"
	req := &entity.AsyncRequest{
		CorrelationID: corrID,
		Payload:       map[string]interface{}{"foo": "data"},
	}
	called := false
	callback := func(reply *entity.AsyncReply) {
		called = true
	}

	err := manager.StoreCorrelation(ctx, corrID, req, callback)
	assert.NoError(t, err)

	gotReq, gotCallback, err := manager.GetCorrelation(ctx, corrID)
	assert.NoError(t, err)
	assert.Equal(t, req.CorrelationID, gotReq.CorrelationID)
	assert.Equal(t, req.Payload, gotReq.Payload)
	assert.NotNil(t, gotCallback)

	// invoke callback
	reply := &entity.AsyncReply{CorrelationID: corrID, Status: entity.AsyncRequestStatusCompleted}
	gotCallback(reply)
	assert.True(t, called)
}

func TestRemoveCorrelation(t *testing.T) {
	manager, mr := setupRedisManager(t, 5*time.Second)
	defer mr.Close()

	ctx := context.Background()
	corrID := "corr2"
	req := &entity.AsyncRequest{CorrelationID: corrID}
	callback := func(reply *entity.AsyncReply) {}

	err := manager.StoreCorrelation(ctx, corrID, req, callback)
	assert.NoError(t, err)

	count, err := manager.GetPendingCount(ctx)
	assert.NoError(t, err)
	assert.Equal(t, 1, count)

	err = manager.RemoveCorrelation(ctx, corrID)
	assert.NoError(t, err)
	count, err = manager.GetPendingCount(ctx)
	assert.NoError(t, err)
	assert.Equal(t, 0, count)
}

func TestGetPendingCount(t *testing.T) {
	manager, mr := setupRedisManager(t, 5*time.Second)
	defer mr.Close()
	ctx := context.Background()

	for i := 0; i < 3; i++ {
		corrID := fmt.Sprintf("p%d", i)
		manager.StoreCorrelation(ctx, corrID, &entity.AsyncRequest{CorrelationID: corrID}, nil)
	}
	count, err := manager.GetPendingCount(ctx)
	assert.NoError(t, err)
	assert.Equal(t, 3, count)
}

func TestGetAllPending(t *testing.T) {
	manager, mr := setupRedisManager(t, 5*time.Second)
	defer mr.Close()
	ctx := context.Background()

	req1 := &entity.AsyncRequest{CorrelationID: "a1"}
	req2 := &entity.AsyncRequest{CorrelationID: "a2"}

	manager.StoreCorrelation(ctx, "a1", req1, nil)
	manager.StoreCorrelation(ctx, "a2", req2, nil)

	pending, err := manager.GetAllPending(ctx)
	assert.NoError(t, err)
	assert.Len(t, pending, 2)
	assert.Equal(t, "a1", pending["a1"].CorrelationID)
	assert.Equal(t, "a2", pending["a2"].CorrelationID)
}

func TestCleanupExpired(t *testing.T) {
	manager, mr := setupRedisManager(t, 1*time.Second)
	defer mr.Close()
	ctx := context.Background()
	corrID := "e1"
	manager.StoreCorrelation(ctx, corrID, &entity.AsyncRequest{CorrelationID: corrID}, nil)
	// expire key
	mr.FastForward(2 * time.Second)
	err := manager.CleanupExpired(ctx)
	assert.NoError(t, err)
	count, err := manager.GetPendingCount(ctx)
	assert.NoError(t, err)
	assert.Equal(t, 0, count)
}

func TestGetCorrelationKeyAndExtract(t *testing.T) {
	// no need for redis here
	client := redis.NewClient(&redis.Options{Addr: ""})
	// cast to concrete type to access key and extract methods
	rm := NewRedisCorrelationManager(client, zap.NewNop(), "myprefix", time.Second).(*RedisCorrelationManager)
	key := rm.GetCorrelationKey("id123")
	assert.Equal(t, "myprefix:correlation:id123", key)
	id := rm.ExtractCorrelationID(key)
	assert.Equal(t, "id123", id)
	// wrong format
	assert.Empty(t, rm.ExtractCorrelationID("wrong:key"))
}
