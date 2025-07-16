package port

import (
	"context"
	"time"

	entity "github.com/quantuanhuy/lib/src/core/entity/async"
)

type ICorrelationManagerPort interface {
	// StoreCorrelation stores a correlation between request and reply
	StoreCorrelation(ctx context.Context, correlationID string, request *entity.AsyncRequest, callback entity.AsyncCallback) error

	// GetCorrelation gets correlation data by correlation ID
	GetCorrelation(ctx context.Context, correlationID string) (*entity.AsyncRequest, entity.AsyncCallback, error)

	// RemoveCorrelation removes correlation data
	RemoveCorrelation(ctx context.Context, correlationID string) error

	// SetTimeout sets timeout for a correlation
	SetTimeout(ctx context.Context, correlationID string, timeout time.Duration) error

	// CleanupExpired cleans up expired correlations
	CleanupExpired(ctx context.Context) error

	// GetPendingCount gets count of pending correlations
	GetPendingCount(ctx context.Context) (int, error)

	// GetAllPending gets all pending correlations
	GetAllPending(ctx context.Context) (map[string]*entity.AsyncRequest, error)
}
