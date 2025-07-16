package async

import (
	"fmt"
	"testing"

	"github.com/stretchr/testify/assert"

	entity "github.com/quantuanhuy/lib/src/core/entity/async"
)

func TestCallbackManager_StoreAndGetCallback(t *testing.T) {
	manager := NewCallbackManager()

	// Test storing and retrieving callback
	correlationID := "test-correlation-1"
	callbackCalled := false

	callback := func(reply *entity.AsyncReply) {
		callbackCalled = true
	}

	manager.StoreCallback(correlationID, callback)

	retrievedCallback, exists := manager.GetCallback(correlationID)
	assert.True(t, exists)
	assert.NotNil(t, retrievedCallback)

	// Test callback execution
	reply := &entity.AsyncReply{
		CorrelationID: correlationID,
		Status:        entity.AsyncRequestStatusCompleted,
	}

	retrievedCallback(reply)
	assert.True(t, callbackCalled)
}

func TestCallbackManager_HasCallback(t *testing.T) {
	manager := NewCallbackManager()

	correlationID := "test-correlation-2"

	// Should not have callback initially
	assert.False(t, manager.HasCallback(correlationID))

	// Store callback
	callback := func(reply *entity.AsyncReply) {}
	manager.StoreCallback(correlationID, callback)

	// Should have callback now
	assert.True(t, manager.HasCallback(correlationID))
}

func TestCallbackManager_RemoveCallback(t *testing.T) {
	manager := NewCallbackManager()

	correlationID := "test-correlation-3"
	callback := func(reply *entity.AsyncReply) {}

	manager.StoreCallback(correlationID, callback)
	assert.True(t, manager.HasCallback(correlationID))

	manager.RemoveCallback(correlationID)
	assert.False(t, manager.HasCallback(correlationID))

	_, exists := manager.GetCallback(correlationID)
	assert.False(t, exists)
}

func TestCallbackManager_NilCallback(t *testing.T) {
	manager := NewCallbackManager()

	correlationID := "test-correlation-4"

	// Store nil callback
	manager.StoreCallback(correlationID, nil)

	// Should not have callback
	assert.False(t, manager.HasCallback(correlationID))

	_, exists := manager.GetCallback(correlationID)
	assert.False(t, exists)
}

func TestCallbackManager_GetCallbackCount(t *testing.T) {
	manager := NewCallbackManager()

	assert.Equal(t, 0, manager.GetCallbackCount())

	callback1 := func(reply *entity.AsyncReply) {}
	callback2 := func(reply *entity.AsyncReply) {}

	manager.StoreCallback("corr-1", callback1)
	assert.Equal(t, 1, manager.GetCallbackCount())

	manager.StoreCallback("corr-2", callback2)
	assert.Equal(t, 2, manager.GetCallbackCount())

	manager.RemoveCallback("corr-1")
	assert.Equal(t, 1, manager.GetCallbackCount())

	manager.RemoveCallback("corr-2")
	assert.Equal(t, 0, manager.GetCallbackCount())
}

func TestCallbackManager_ConcurrentAccess(t *testing.T) {
	manager := NewCallbackManager()

	// Test concurrent access
	done := make(chan bool, 2)

	go func() {
		for i := 0; i < 100; i++ {
			correlationID := fmt.Sprintf("corr-%d", i)
			callback := func(reply *entity.AsyncReply) {}
			manager.StoreCallback(correlationID, callback)
		}
		done <- true
	}()

	go func() {
		for i := 0; i < 100; i++ {
			correlationID := fmt.Sprintf("corr-%d", i)
			manager.GetCallback(correlationID)
			manager.HasCallback(correlationID)
		}
		done <- true
	}()

	// Wait for both goroutines to complete
	<-done
	<-done

	assert.Equal(t, 100, manager.GetCallbackCount())
}
