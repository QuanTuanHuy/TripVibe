package port

import (
	"chat_service/core/domain/constant"
	"chat_service/core/domain/dto/request"
	"chat_service/core/domain/dto/response"
	"chat_service/core/domain/entity"
	"context"
	"testing"

	"gorm.io/gorm"
)

// MockMessagePort is a mock implementation of IMessagePort to test interface compliance
type MockMessagePort struct{}

// Existing methods
func (m *MockMessagePort) CreateMessage(ctx context.Context, tx *gorm.DB, message *entity.MessageEntity) (*entity.MessageEntity, error) {
	return nil, nil
}

func (m *MockMessagePort) GetMessagesByRoomId(ctx context.Context, chatRoomID int64, params *request.MessageQueryParams) ([]*entity.MessageEntity, *response.PaginationResult, error) {
	return nil, nil, nil
}

func (m *MockMessagePort) GetMessagesByIDs(ctx context.Context, messageIDs []int64) ([]*entity.MessageEntity, error) {
	return nil, nil
}

func (m *MockMessagePort) MarkMessageAsRead(ctx context.Context, tx *gorm.DB, roomID, userID, messageID int64) error {
	return nil
}

func (m *MockMessagePort) CountUnreadMessages(ctx context.Context, roomID, userID int64) (int64, error) {
	return 0, nil
}

// New status-related methods (Task 1.2.2)
func (m *MockMessagePort) UpdateMessageStatus(ctx context.Context, tx *gorm.DB, messageID int64, status constant.MessageStatus, userID int64) error {
	return nil
}

func (m *MockMessagePort) GetUnreadMessages(ctx context.Context, roomID, userID int64, params *request.MessageQueryParams) ([]*entity.MessageEntity, *response.PaginationResult, error) {
	return nil, nil, nil
}

func (m *MockMessagePort) MarkMessagesAsDelivered(ctx context.Context, tx *gorm.DB, roomID, userID int64, messageIDs []int64) error {
	return nil
}

func (m *MockMessagePort) GetMessagesByStatus(ctx context.Context, roomID int64, status constant.MessageStatus, params *request.MessageQueryParams) ([]*entity.MessageEntity, *response.PaginationResult, error) {
	return nil, nil, nil
}

// TestMessagePortInterface verifies that MockMessagePort implements IMessagePort interface
func TestMessagePortInterface(t *testing.T) {
	var _ IMessagePort = &MockMessagePort{}
	t.Log("MockMessagePort successfully implements IMessagePort interface")
}

// TestNewMethodSignatures verifies the new method signatures are correct
func TestNewMethodSignatures(t *testing.T) {
	mock := &MockMessagePort{}
	ctx := context.Background()

	// Test UpdateMessageStatus
	err := mock.UpdateMessageStatus(ctx, nil, 1, constant.MessageStatusDelivered, 1)
	if err != nil {
		t.Errorf("UpdateMessageStatus failed: %v", err)
	}

	// Test GetUnreadMessages
	messages, pagination, err := mock.GetUnreadMessages(ctx, 1, 1, &request.MessageQueryParams{})
	if err != nil {
		t.Errorf("GetUnreadMessages failed: %v", err)
	}
	if messages == nil || pagination == nil {
		t.Log("GetUnreadMessages returned expected nil values for mock")
	}

	// Test MarkMessagesAsDelivered
	err = mock.MarkMessagesAsDelivered(ctx, nil, 1, 1, []int64{1, 2, 3})
	if err != nil {
		t.Errorf("MarkMessagesAsDelivered failed: %v", err)
	}

	// Test GetMessagesByStatus
	messages, pagination, err = mock.GetMessagesByStatus(ctx, 1, constant.MessageStatusRead, &request.MessageQueryParams{})
	if err != nil {
		t.Errorf("GetMessagesByStatus failed: %v", err)
	}
	if messages == nil || pagination == nil {
		t.Log("GetMessagesByStatus returned expected nil values for mock")
	}

	t.Log("All new method signatures are working correctly")
}
