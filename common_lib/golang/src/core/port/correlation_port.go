package port

import (
	"context"
	"time"

	entity "github.com/quantuanhuy/lib/src/core/entity/async"
)

type ICorrelationPort interface {
	StoreCorrelation(ctx context.Context, correlationID string, request *entity.AsyncRequest, callback entity.AsyncCallback) error

	GetCorrelation(ctx context.Context, correlationID string) (*entity.AsyncRequest, entity.AsyncCallback, error)

	RemoveCorrelation(ctx context.Context, correlationID string) error

	SetTimeout(ctx context.Context, correlationID string, timeout time.Duration) error

	GetPendingCount(ctx context.Context) (int, error)

	GetAllPending(ctx context.Context) (map[string]*entity.AsyncRequest, error)
}
