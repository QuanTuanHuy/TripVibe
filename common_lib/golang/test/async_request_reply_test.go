package async

import (
	"context"
	"testing"
	"time"

	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/mock"

	entity "github.com/quantuanhuy/lib/src/core/entity/async"
)

// MockAsyncRequestManager is a mock implementation for testing
type MockAsyncRequestManager struct {
	mock.Mock
}

func (m *MockAsyncRequestManager) SendRequest(ctx context.Context, requestType string, payload map[string]interface{}, timeout time.Duration) (string, error) {
	args := m.Called(ctx, requestType, payload, timeout)
	return args.String(0), args.Error(1)
}

func (m *MockAsyncRequestManager) SendRequestWithCallback(ctx context.Context, requestType string, payload map[string]interface{}, timeout time.Duration, callback entity.AsyncCallback) (string, error) {
	args := m.Called(ctx, requestType, payload, timeout, callback)
	return args.String(0), args.Error(1)
}

func (m *MockAsyncRequestManager) WaitForReply(ctx context.Context, correlationID string, timeout time.Duration) (*entity.AsyncReply, error) {
	args := m.Called(ctx, correlationID, timeout)
	return args.Get(0).(*entity.AsyncReply), args.Error(1)
}

func (m *MockAsyncRequestManager) CancelRequest(ctx context.Context, correlationID string) error {
	args := m.Called(ctx, correlationID)
	return args.Error(0)
}

func (m *MockAsyncRequestManager) GetRequestStatus(ctx context.Context, correlationID string) (entity.AsyncRequestStatus, error) {
	args := m.Called(ctx, correlationID)
	return args.Get(0).(entity.AsyncRequestStatus), args.Error(1)
}

func (m *MockAsyncRequestManager) RegisterHandler(requestType string, handler entity.AsyncRequestHandler) {
	m.Called(requestType, handler)
}

func (m *MockAsyncRequestManager) UnregisterHandler(requestType string) {
	m.Called(requestType)
}

func (m *MockAsyncRequestManager) StartProcessing(ctx context.Context) error {
	args := m.Called(ctx)
	return args.Error(0)
}

func (m *MockAsyncRequestManager) StopProcessing(ctx context.Context) error {
	args := m.Called(ctx)
	return args.Error(0)
}

// TestAsyncRequestEntity tests the AsyncRequest entity
func TestAsyncRequestEntity(t *testing.T) {
	t.Run("CreateAsyncRequest", func(t *testing.T) {
		request := &entity.AsyncRequest{
			ID:            "req-001",
			CorrelationID: "corr-001",
			RequestType:   "test-request",
			Status:        entity.AsyncRequestStatusPending,
			Payload: map[string]interface{}{
				"key": "value",
			},
			ReplyTo:   "reply-topic",
			Timeout:   30 * time.Second,
			CreatedAt: time.Now(),
			UpdatedAt: time.Now(),
		}

		assert.NotNil(t, request)
		assert.Equal(t, "req-001", request.ID)
		assert.Equal(t, "corr-001", request.CorrelationID)
		assert.Equal(t, "test-request", request.RequestType)
		assert.Equal(t, entity.AsyncRequestStatusPending, request.Status)
		assert.Equal(t, "value", request.Payload["key"])
		assert.Equal(t, "reply-topic", request.ReplyTo)
		assert.Equal(t, 30*time.Second, request.Timeout)
	})
}

// TestAsyncReplyEntity tests the AsyncReply entity
func TestAsyncReplyEntity(t *testing.T) {
	t.Run("CreateAsyncReply", func(t *testing.T) {
		reply := &entity.AsyncReply{
			ID:            "reply-001",
			CorrelationID: "corr-001",
			RequestID:     "req-001",
			Status:        entity.AsyncRequestStatusCompleted,
			Result: map[string]interface{}{
				"result": "success",
			},
			ProcessedAt: time.Now(),
			Duration:    100 * time.Millisecond,
		}

		assert.NotNil(t, reply)
		assert.Equal(t, "reply-001", reply.ID)
		assert.Equal(t, "corr-001", reply.CorrelationID)
		assert.Equal(t, "req-001", reply.RequestID)
		assert.Equal(t, entity.AsyncRequestStatusCompleted, reply.Status)
		assert.Equal(t, "success", reply.Result["result"])
		assert.Equal(t, 100*time.Millisecond, reply.Duration)
	})
}

// TestAsyncMessageEntity tests the AsyncMessage entity
func TestAsyncMessageEntity(t *testing.T) {
	t.Run("CreateAsyncMessage", func(t *testing.T) {
		message := &entity.AsyncMessage{
			ID:            "msg-001",
			Type:          entity.MessageTypeRequest,
			CorrelationID: "corr-001",
			RequestType:   "test-request",
			ReplyTo:       "reply-topic",
			Payload: map[string]interface{}{
				"data": "test",
			},
			Timestamp: time.Now(),
			TTL:       30 * time.Second,
		}

		assert.NotNil(t, message)
		assert.Equal(t, "msg-001", message.ID)
		assert.Equal(t, entity.MessageTypeRequest, message.Type)
		assert.Equal(t, "corr-001", message.CorrelationID)
		assert.Equal(t, "test-request", message.RequestType)
		assert.Equal(t, "reply-topic", message.ReplyTo)
		assert.Equal(t, "test", message.Payload["data"])
		assert.Equal(t, 30*time.Second, message.TTL)
	})

	t.Run("SerializeAsyncMessage", func(t *testing.T) {
		message := &entity.AsyncMessage{
			ID:            "msg-001",
			Type:          entity.MessageTypeRequest,
			CorrelationID: "corr-001",
			RequestType:   "test-request",
			Payload: map[string]interface{}{
				"data": "test",
			},
			Timestamp: time.Now(),
		}

		// Test serialization
		data, err := message.ToJSON()
		assert.NoError(t, err)
		assert.NotEmpty(t, data)

		// Test deserialization
		parsedMessage, err := entity.FromJSON(data)
		assert.NoError(t, err)
		assert.Equal(t, message.ID, parsedMessage.ID)
		assert.Equal(t, message.Type, parsedMessage.Type)
		assert.Equal(t, message.CorrelationID, parsedMessage.CorrelationID)
		assert.Equal(t, message.RequestType, parsedMessage.RequestType)
		assert.Equal(t, message.Payload["data"], parsedMessage.Payload["data"])
	})
}

// TestAsyncRequestManager tests the async request manager interface
func TestAsyncRequestManager(t *testing.T) {
	t.Run("SendRequest", func(t *testing.T) {
		mockManager := new(MockAsyncRequestManager)
		ctx := context.Background()

		// Setup mock expectations
		mockManager.On("SendRequest", ctx, "test-request", mock.AnythingOfType("map[string]interface {}"), 30*time.Second).
			Return("corr-001", nil)

		// Test send request
		correlationID, err := mockManager.SendRequest(ctx, "test-request", map[string]interface{}{
			"key": "value",
		}, 30*time.Second)

		assert.NoError(t, err)
		assert.Equal(t, "corr-001", correlationID)

		// Verify mock expectations
		mockManager.AssertExpectations(t)
	})

	t.Run("WaitForReply", func(t *testing.T) {
		mockManager := new(MockAsyncRequestManager)
		ctx := context.Background()

		expectedReply := &entity.AsyncReply{
			ID:            "reply-001",
			CorrelationID: "corr-001",
			Status:        entity.AsyncRequestStatusCompleted,
			Result: map[string]interface{}{
				"result": "success",
			},
			ProcessedAt: time.Now(),
		}

		// Setup mock expectations
		mockManager.On("WaitForReply", ctx, "corr-001", 30*time.Second).
			Return(expectedReply, nil)

		// Test wait for reply
		reply, err := mockManager.WaitForReply(ctx, "corr-001", 30*time.Second)

		assert.NoError(t, err)
		assert.Equal(t, expectedReply.ID, reply.ID)
		assert.Equal(t, expectedReply.CorrelationID, reply.CorrelationID)
		assert.Equal(t, expectedReply.Status, reply.Status)
		assert.Equal(t, expectedReply.Result["result"], reply.Result["result"])

		// Verify mock expectations
		mockManager.AssertExpectations(t)
	})

	t.Run("SendRequestWithCallback", func(t *testing.T) {
		mockManager := new(MockAsyncRequestManager)
		ctx := context.Background()

		callback := func(reply *entity.AsyncReply) {
			// Callback implementation
		}

		// Setup mock expectations
		mockManager.On("SendRequestWithCallback", ctx, "test-request", mock.AnythingOfType("map[string]interface {}"), 30*time.Second, mock.AnythingOfType("entity.AsyncCallback")).
			Return("corr-001", nil)

		// Test send request with callback
		correlationID, err := mockManager.SendRequestWithCallback(ctx, "test-request", map[string]interface{}{
			"key": "value",
		}, 30*time.Second, callback)

		assert.NoError(t, err)
		assert.Equal(t, "corr-001", correlationID)

		// Verify mock expectations
		mockManager.AssertExpectations(t)
	})

	t.Run("CancelRequest", func(t *testing.T) {
		mockManager := new(MockAsyncRequestManager)
		ctx := context.Background()

		// Setup mock expectations
		mockManager.On("CancelRequest", ctx, "corr-001").
			Return(nil)

		// Test cancel request
		err := mockManager.CancelRequest(ctx, "corr-001")

		assert.NoError(t, err)

		// Verify mock expectations
		mockManager.AssertExpectations(t)
	})

	t.Run("GetRequestStatus", func(t *testing.T) {
		mockManager := new(MockAsyncRequestManager)
		ctx := context.Background()

		// Setup mock expectations
		mockManager.On("GetRequestStatus", ctx, "corr-001").
			Return(entity.AsyncRequestStatusCompleted, nil)

		// Test get request status
		status, err := mockManager.GetRequestStatus(ctx, "corr-001")

		assert.NoError(t, err)
		assert.Equal(t, entity.AsyncRequestStatusCompleted, status)

		// Verify mock expectations
		mockManager.AssertExpectations(t)
	})
}

// TestAsyncRequestHandler tests async request handlers
func TestAsyncRequestHandler(t *testing.T) {
	t.Run("HandlerExecution", func(t *testing.T) {
		handler := func(request *entity.AsyncRequest) (*entity.AsyncReply, error) {
			return &entity.AsyncReply{
				ID:            "reply-001",
				CorrelationID: request.CorrelationID,
				RequestID:     request.ID,
				Status:        entity.AsyncRequestStatusCompleted,
				Result: map[string]interface{}{
					"processed": true,
					"input":     request.Payload["input"],
				},
				ProcessedAt: time.Now(),
				Duration:    100 * time.Millisecond,
			}, nil
		}

		request := &entity.AsyncRequest{
			ID:            "req-001",
			CorrelationID: "corr-001",
			RequestType:   "test-request",
			Payload: map[string]interface{}{
				"input": "test-data",
			},
			CreatedAt: time.Now(),
		}

		reply, err := handler(request)

		assert.NoError(t, err)
		assert.NotNil(t, reply)
		assert.Equal(t, "reply-001", reply.ID)
		assert.Equal(t, "corr-001", reply.CorrelationID)
		assert.Equal(t, "req-001", reply.RequestID)
		assert.Equal(t, entity.AsyncRequestStatusCompleted, reply.Status)
		assert.Equal(t, true, reply.Result["processed"])
		assert.Equal(t, "test-data", reply.Result["input"])
		assert.Equal(t, 100*time.Millisecond, reply.Duration)
	})
}

// TestAsyncRequestStatuses tests different async request statuses
func TestAsyncRequestStatuses(t *testing.T) {
	statuses := []entity.AsyncRequestStatus{
		entity.AsyncRequestStatusPending,
		entity.AsyncRequestStatusProcessing,
		entity.AsyncRequestStatusCompleted,
		entity.AsyncRequestStatusFailed,
		entity.AsyncRequestStatusTimeout,
		entity.AsyncRequestStatusCancelled,
	}

	for _, status := range statuses {
		t.Run(string(status), func(t *testing.T) {
			request := &entity.AsyncRequest{
				ID:            "req-001",
				CorrelationID: "corr-001",
				Status:        status,
				CreatedAt:     time.Now(),
			}

			assert.Equal(t, status, request.Status)
		})
	}
}

// TestMessageTypes tests different message types
func TestMessageTypes(t *testing.T) {
	messageTypes := []entity.MessageType{
		entity.MessageTypeRequest,
		entity.MessageTypeReply,
		entity.MessageTypeHeartbeat,
		entity.MessageTypeCancel,
	}

	for _, msgType := range messageTypes {
		t.Run(string(msgType), func(t *testing.T) {
			message := &entity.AsyncMessage{
				ID:            "msg-001",
				Type:          msgType,
				CorrelationID: "corr-001",
				Timestamp:     time.Now(),
			}

			assert.Equal(t, msgType, message.Type)
		})
	}
}
