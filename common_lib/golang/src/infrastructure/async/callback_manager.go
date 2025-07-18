package async

import (
	"sync"

	entity "github.com/quantuanhuy/lib/src/core/entity/async"
)

type CallbackManager struct {
	callbacks map[string]entity.AsyncCallback
	mutext    sync.RWMutex
}

func NewCallbackManager() *CallbackManager {
	return &CallbackManager{
		callbacks: make(map[string]entity.AsyncCallback),
	}
}

func (cm *CallbackManager) StoreCallback(correlationID string, callback entity.AsyncCallback) {
	cm.mutext.Lock()
	defer cm.mutext.Unlock()

	if callback != nil {
		cm.callbacks[correlationID] = callback
	}
}

func (cm *CallbackManager) GetCallback(correlationID string) (entity.AsyncCallback, bool) {
	cm.mutext.RLock()
	defer cm.mutext.RUnlock()

	callback, exists := cm.callbacks[correlationID]
	return callback, exists
}

func (cm *CallbackManager) HasCallback(correlationID string) bool {
	_, exists := cm.GetCallback(correlationID)
	return exists
}

func (cm *CallbackManager) RemoveCallback(correlationID string) {
	cm.mutext.Lock()
	defer cm.mutext.Unlock()
	delete(cm.callbacks, correlationID)
}

func (cm *CallbackManager) GetCallbackCount() int {
	cm.mutext.RLock()
	defer cm.mutext.RUnlock()

	return len(cm.callbacks)
}
