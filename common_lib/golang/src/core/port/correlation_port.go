package port

import (
	"context"
	"time"

	entity "github.com/quantuanhuy/lib/src/core/entity/async"
)

// ICorrelationPort manages correlation data for async requests
type ICorrelationPort interface {
	StoreCorrelation(ctx context.Context, correlationID string, data *entity.CorrelationData) error

	GetCorrelation(ctx context.Context, correlationID string) (*entity.CorrelationData, error)

	UpdateCorrelation(ctx context.Context, correlationID string, data *entity.CorrelationData) error

	RemoveCorrelation(ctx context.Context, correlationID string) error

	SetTimeout(ctx context.Context, correlationID string, timeout time.Duration) error

	GetPendingCount(ctx context.Context) (int, error)

	GetAllPending(ctx context.Context) (map[string]*entity.CorrelationData, error)

	CleanupExpired(ctx context.Context) error
}

// ICallbackManager manages callback functions for async requests
type ICallbackManager interface {
	StoreCallback(correlationID string, callback entity.AsyncCallback)
	GetCallback(correlationID string) (entity.AsyncCallback, bool)
	RemoveCallback(correlationID string)
	HasCallback(correlationID string) bool
	GetCallbackCount() int
}
