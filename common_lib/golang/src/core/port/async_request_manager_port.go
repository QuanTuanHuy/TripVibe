package port

import (
	"context"
	"time"

	entity "github.com/quantuanhuy/lib/src/core/entity/async"
)

type IAsyncRequestManagerPort interface {
	// SendRequest sends an async request and returns correlation ID
	SendRequest(ctx context.Context, requestType string, payload map[string]interface{}, timeout time.Duration) (string, error)

	// SendRequestWithCallback sends an async request with callback
	SendRequestWithCallback(ctx context.Context, requestType string, payload map[string]interface{}, timeout time.Duration, callback entity.AsyncCallback) (string, error)

	// WaitForReply waits for a reply with correlation ID
	WaitForReply(ctx context.Context, correlationID string, timeout time.Duration) (*entity.AsyncReply, error)

	// CancelRequest cancels a pending request
	CancelRequest(ctx context.Context, correlationID string) error

	// GetRequestStatus gets the status of a request
	GetRequestStatus(ctx context.Context, correlationID string) (entity.AsyncRequestStatus, error)

	// RegisterHandler registers a handler for a request type
	RegisterHandler(requestType string, handler entity.AsyncRequestHandler)

	// UnregisterHandler unregisters a handler for a request type
	UnregisterHandler(requestType string)

	// StartProcessing starts processing async requests
	StartProcessing(ctx context.Context) error

	// StopProcessing stops processing async requests
	StopProcessing(ctx context.Context) error
}
