package port

import (
	"context"
	"time"

	entity "github.com/quantuanhuy/lib/src/core/entity/async"
)

// IAsyncRequestManager manages async request-reply operations
type IAsyncRequestManager interface {
	// Send async request without callback
	SendRequest(ctx context.Context, requestType string, payload map[string]interface{}, timeout time.Duration) (string, error)

	// Send async request with callback
	SendRequestWithCallback(ctx context.Context, requestType string, payload map[string]interface{}, timeout time.Duration, callback entity.AsyncCallback) (string, error)

	// Wait for reply synchronously
	WaitForReply(ctx context.Context, correlationID string, timeout time.Duration) (*entity.AsyncReply, error)

	// Cancel pending request
	CancelRequest(ctx context.Context, correlationID string) error

	// Get request status
	GetRequestStatus(ctx context.Context, correlationID string) (entity.AsyncRequestStatus, error)

	// Lifecycle management
	Start(ctx context.Context) error
	Stop(ctx context.Context) error
	IsRunning() bool
}

// IAsyncRequestHandler handles incoming async requests
type IAsyncRequestHandler interface {
	// Register handler for specific request type
	RegisterHandler(requestType string, handler entity.AsyncRequestHandler)
	
	// Unregister handler
	UnregisterHandler(requestType string)
	
	// Handle incoming request message
	HandleRequest(ctx context.Context, request *entity.AsyncRequest) (*entity.AsyncReply, error)
}

// IAsyncReplyHandler handles incoming async replies
type IAsyncReplyHandler interface {
	// Handle incoming reply message
	HandleReply(ctx context.Context, reply *entity.AsyncReply) error
}
