package async

import (
	"sync"

	entity "github.com/quantuanhuy/lib/src/core/entity/async"
	"github.com/quantuanhuy/lib/src/core/port"
)

type CallbackManager struct {
	callbacks map[string]entity.AsyncCallback
	mutex     sync.RWMutex
}

func NewCallbackManager() port.ICallbackManager {
	return &CallbackManager{
		callbacks: make(map[string]entity.AsyncCallback),
	}
}

func (cm *CallbackManager) StoreCallback(correlationID string, callback entity.AsyncCallback) {
	if callback == nil {
		return
	}
	
	cm.mutex.Lock()
	defer cm.mutex.Unlock()
	
	cm.callbacks[correlationID] = callback
}

func (cm *CallbackManager) GetCallback(correlationID string) (entity.AsyncCallback, bool) {
	cm.mutex.RLock()
	defer cm.mutex.RUnlock()

	callback, exists := cm.callbacks[correlationID]
	return callback, exists
}

func (cm *CallbackManager) HasCallback(correlationID string) bool {
	cm.mutex.RLock()
	defer cm.mutex.RUnlock()
	
	_, exists := cm.callbacks[correlationID]
	return exists
}

func (cm *CallbackManager) RemoveCallback(correlationID string) {
	cm.mutex.Lock()
	defer cm.mutex.Unlock()
	
	delete(cm.callbacks, correlationID)
}

func (cm *CallbackManager) GetCallbackCount() int {
	cm.mutex.RLock()
	defer cm.mutex.RUnlock()

	return len(cm.callbacks)
}
