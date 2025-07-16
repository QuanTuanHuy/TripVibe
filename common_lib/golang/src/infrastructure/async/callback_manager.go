package async

import (
	"sync"

	entity "github.com/quantuanhuy/lib/src/core/entity/async"
)

// CallbackManager manages callbacks in memory
type CallbackManager struct {
	callbacks map[string]entity.AsyncCallback
	mutex     sync.RWMutex
}

// NewCallbackManager creates a new callback manager
func NewCallbackManager() *CallbackManager {
	return &CallbackManager{
		callbacks: make(map[string]entity.AsyncCallback),
	}
}

// StoreCallback stores a callback for correlation ID
func (c *CallbackManager) StoreCallback(correlationID string, callback entity.AsyncCallback) {
	c.mutex.Lock()
	defer c.mutex.Unlock()

	if callback != nil {
		c.callbacks[correlationID] = callback
	}
}

// GetCallback retrieves a callback for correlation ID
func (c *CallbackManager) GetCallback(correlationID string) (entity.AsyncCallback, bool) {
	c.mutex.RLock()
	defer c.mutex.RUnlock()

	callback, exists := c.callbacks[correlationID]
	return callback, exists
}

// RemoveCallback removes a callback for correlation ID
func (c *CallbackManager) RemoveCallback(correlationID string) {
	c.mutex.Lock()
	defer c.mutex.Unlock()

	delete(c.callbacks, correlationID)
}

// HasCallback checks if callback exists for correlation ID
func (c *CallbackManager) HasCallback(correlationID string) bool {
	c.mutex.RLock()
	defer c.mutex.RUnlock()

	_, exists := c.callbacks[correlationID]
	return exists
}

// GetCallbackCount returns the number of stored callbacks
func (c *CallbackManager) GetCallbackCount() int {
	c.mutex.RLock()
	defer c.mutex.RUnlock()

	return len(c.callbacks)
}
