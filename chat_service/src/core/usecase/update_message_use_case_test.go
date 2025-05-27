package usecase

import (
	"chat_service/core/domain/constant"
	"chat_service/core/domain/dto/request"
	"chat_service/core/domain/dto/response"
	"chat_service/core/domain/entity"
	"context"
	"errors"
	"testing"

	"gorm.io/gorm"
)

// Mock implementations for testing
type mockMessagePort struct {
	messages         map[int64]*entity.MessageEntity
	statusUpdates    []statusUpdate
	lastUpdateUserID int64 // Track the last userID used for update operations
}

type statusUpdate struct {
	messageID int64
	status    constant.MessageStatus
	userID    int64
}

func (m *mockMessagePort) CreateMessage(ctx context.Context, tx *gorm.DB, message *entity.MessageEntity) (*entity.MessageEntity, error) {
	return nil, nil
}

func (m *mockMessagePort) GetMessageById(ctx context.Context, messageId int64) (*entity.MessageEntity, error) {
	if msg, exists := m.messages[messageId]; exists {
		return msg, nil
	}
	return nil, errors.New("message not found")
}

func (m *mockMessagePort) GetMessagesByIDs(ctx context.Context, messageIDs []int64) ([]*entity.MessageEntity, error) {
	var result []*entity.MessageEntity
	for _, id := range messageIDs {
		if msg, exists := m.messages[id]; exists {
			result = append(result, msg)
		}
	}
	if len(result) == 0 {
		return nil, errors.New("no messages found")
	}
	return result, nil
}

func (m *mockMessagePort) GetMessagesByRoomId(ctx context.Context, chatRoomID int64, params *request.MessageQueryParams) ([]*entity.MessageEntity, *response.PaginationResult, error) {
	return nil, &response.PaginationResult{}, nil
}

func (m *mockMessagePort) UpdateMessage(ctx context.Context, tx *gorm.DB, message *entity.MessageEntity) error {
	// Track status updates for test verification
	m.statusUpdates = append(m.statusUpdates, statusUpdate{
		messageID: message.ID,
		status:    message.Status,
		userID:    m.lastUpdateUserID, // Use the last userID set by the test context
	})

	// Update the message in memory
	if _, exists := m.messages[message.ID]; exists {
		m.messages[message.ID] = message
		return nil
	}
	return errors.New("message not found")
}

// SetUpdateUserID sets the userID context for the next update operation (for testing)
func (m *mockMessagePort) SetUpdateUserID(userID int64) {
	m.lastUpdateUserID = userID
}

func (m *mockMessagePort) GetUnreadMessages(ctx context.Context, roomID, userID int64, params *request.MessageQueryParams) ([]*entity.MessageEntity, *response.PaginationResult, error) {
	return nil, &response.PaginationResult{}, nil
}

func (m *mockMessagePort) GetMessagesByStatus(ctx context.Context, roomID int64, status constant.MessageStatus, params *request.MessageQueryParams) ([]*entity.MessageEntity, *response.PaginationResult, error) {
	var result []*entity.MessageEntity
	for _, msg := range m.messages {
		if msg.ChatRoomID == roomID && msg.Status == status {
			result = append(result, msg)
		}
	}
	return result, &response.PaginationResult{}, nil
}

func (m *mockMessagePort) CountUnreadMessages(ctx context.Context, roomID, userID int64) (int64, error) {
	count := int64(0)
	for _, msg := range m.messages {
		if msg.ChatRoomID == roomID && (msg.Status == constant.MessageStatusSent || msg.Status == constant.MessageStatusDelivered) {
			// Check if message is not from the user (only count others' messages as unread)
			if msg.SenderID != nil && *msg.SenderID != userID {
				count++
			}
		}
	}
	return count, nil
}

func (m *mockMessagePort) MarkMessageAsRead(ctx context.Context, tx *gorm.DB, roomID, userID, messageID int64) error {
	// Find and update the message as read
	for _, msg := range m.messages {
		if msg.ID == messageID && msg.ChatRoomID == roomID {
			if msg.SenderID == nil || *msg.SenderID != userID {
				err := msg.UpdateStatus(constant.MessageStatusRead)
				if err != nil {
					return err
				}
			}
			break
		}
	}
	return nil
}

type mockGetChatRoomUseCase struct {
	authorizedRooms map[int64][]int64 // roomID -> []userID
}

func (m *mockGetChatRoomUseCase) GetChatRoomById(ctx context.Context, roomID, userID int64) (*entity.ChatRoomEntity, error) {
	if users, exists := m.authorizedRooms[roomID]; exists {
		for _, authorizedUserID := range users {
			if authorizedUserID == userID {
				return &entity.ChatRoomEntity{
					BaseEntity: entity.BaseEntity{
						ID: roomID,
					},
				}, nil
			}
		}
	}
	return nil, errors.New("unauthorized")
}

func (m *mockGetChatRoomUseCase) GetChatRoomsByUserId(ctx context.Context, userID int64, params *request.ChatRoomQueryParams) ([]*entity.ChatRoomEntity, *response.PaginationResult, error) {
	return nil, &response.PaginationResult{}, nil
}

func (m *mockGetChatRoomUseCase) GetChatRooms(ctx context.Context, params *request.ChatRoomQueryParams) ([]*entity.ChatRoomEntity, error) {
	return nil, nil
}

func (m *mockGetChatRoomUseCase) CountChatRooms(ctx context.Context, params *request.ChatRoomQueryParams) (int64, error) {
	return 0, nil
}

type mockDatabaseTransactionUseCase struct {
	shouldFailCommit bool
}

func (m *mockDatabaseTransactionUseCase) StartTransaction() *gorm.DB {
	return &gorm.DB{}
}

func (m *mockDatabaseTransactionUseCase) Commit(tx *gorm.DB) error {
	if m.shouldFailCommit {
		return errors.New("commit failed")
	}
	return nil
}

func (m *mockDatabaseTransactionUseCase) Rollback(tx *gorm.DB) error {
	return nil
}

type mockEventEmitter struct {
	events []statusChangeEvent
}

type statusChangeEvent struct {
	roomID    int64
	messageID int64
	oldStatus constant.MessageStatus
	newStatus constant.MessageStatus
	updatedBy int64
}

func (m *mockEventEmitter) EmitMessageStatusChange(ctx context.Context, roomID int64, messageID int64, oldStatus, newStatus constant.MessageStatus, updatedBy int64) error {
	m.events = append(m.events, statusChangeEvent{
		roomID:    roomID,
		messageID: messageID,
		oldStatus: oldStatus,
		newStatus: newStatus,
		updatedBy: updatedBy,
	})
	return nil
}

func TestUpdateMessageUseCase_UpdateMessageStatus(t *testing.T) {
	tests := []struct {
		name           string
		messageID      int64
		status         constant.MessageStatus
		userID         int64
		setupMessages  map[int64]*entity.MessageEntity
		authorizedRoom bool
		expectError    bool
		expectedStatus constant.MessageStatus
	}{{
		name:      "valid status update to delivered",
		messageID: 1,
		status:    constant.MessageStatusDelivered,
		userID:    2,
		setupMessages: map[int64]*entity.MessageEntity{
			1: {
				BaseEntity: entity.BaseEntity{
					ID: 1,
				},
				ChatRoomID: 100,
				SenderID:   func() *int64 { id := int64(1); return &id }(),
				Content:    "Hello",
				Status:     constant.MessageStatusSent,
			},
		},
		authorizedRoom: true,
		expectError:    false,
		expectedStatus: constant.MessageStatusDelivered,
	}, {
		name:      "unauthorized user",
		messageID: 1,
		status:    constant.MessageStatusDelivered,
		userID:    3,
		setupMessages: map[int64]*entity.MessageEntity{
			1: {
				BaseEntity: entity.BaseEntity{
					ID: 1,
				},
				ChatRoomID: 100,
				SenderID:   func() *int64 { id := int64(1); return &id }(),
				Content:    "Hello",
				Status:     constant.MessageStatusSent,
			},
		},
		authorizedRoom: false,
		expectError:    true,
	}, {
		name:      "user cannot update own message status (non-read)",
		messageID: 1,
		status:    constant.MessageStatusDelivered,
		userID:    1, // Same as sender
		setupMessages: map[int64]*entity.MessageEntity{
			1: {
				BaseEntity: entity.BaseEntity{
					ID: 1,
				},
				ChatRoomID: 100,
				SenderID:   func() *int64 { id := int64(1); return &id }(),
				Content:    "Hello",
				Status:     constant.MessageStatusSent,
			},
		},
		authorizedRoom: true,
		expectError:    true,
	}, {
		name:      "user can mark own message as read",
		messageID: 1,
		status:    constant.MessageStatusRead,
		userID:    1, // Same as sender
		setupMessages: map[int64]*entity.MessageEntity{
			1: {
				BaseEntity: entity.BaseEntity{
					ID: 1,
				},
				ChatRoomID: 100,
				SenderID:   func() *int64 { id := int64(1); return &id }(),
				Content:    "Hello",
				Status:     constant.MessageStatusDelivered,
			},
		},
		authorizedRoom: true,
		expectError:    false,
		expectedStatus: constant.MessageStatusRead,
	},
		{
			name:           "message not found",
			messageID:      999,
			status:         constant.MessageStatusDelivered,
			userID:         2,
			setupMessages:  map[int64]*entity.MessageEntity{},
			authorizedRoom: true,
			expectError:    true,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			// Setup mocks
			mockMessagePort := &mockMessagePort{
				messages: tt.setupMessages,
			}

			mockChatRoomUseCase := &mockGetChatRoomUseCase{
				authorizedRooms: make(map[int64][]int64),
			}

			if tt.authorizedRoom && len(tt.setupMessages) > 0 {
				for _, msg := range tt.setupMessages {
					mockChatRoomUseCase.authorizedRooms[msg.ChatRoomID] = []int64{tt.userID}
				}
			}

			mockDBUseCase := &mockDatabaseTransactionUseCase{}
			mockEventEmitter := &mockEventEmitter{}

			// Create use case
			useCase := NewUpdateMessageUseCaseWithEvents(
				mockMessagePort,
				mockChatRoomUseCase,
				mockDBUseCase,
				mockEventEmitter,
			)

			// Set the userID context for the mock (for tracking purposes)
			mockMessagePort.SetUpdateUserID(tt.userID)

			// Execute
			result, err := useCase.UpdateMessageStatus(context.Background(), tt.messageID, tt.status, tt.userID)

			// Verify
			if tt.expectError {
				if err == nil {
					t.Errorf("expected error but got none")
				}
				return
			}

			if err != nil {
				t.Errorf("unexpected error: %v", err)
				return
			}

			if result == nil {
				t.Errorf("expected result but got nil")
				return
			}

			if result.Status != tt.expectedStatus {
				t.Errorf("expected status %v but got %v", tt.expectedStatus, result.Status)
			}

			// Verify status update was called
			if len(mockMessagePort.statusUpdates) != 1 {
				t.Errorf("expected 1 status update but got %d", len(mockMessagePort.statusUpdates))
				return
			}

			update := mockMessagePort.statusUpdates[0]
			if update.messageID != tt.messageID || update.status != tt.status || update.userID != tt.userID {
				t.Errorf("status update mismatch: got %+v", update)
			}

			// Verify event was emitted
			if len(mockEventEmitter.events) != 1 {
				t.Errorf("expected 1 event but got %d", len(mockEventEmitter.events))
				return
			}
		})
	}
}

func TestUpdateMessageUseCase_MarkMessagesAsDelivered(t *testing.T) {
	tests := []struct {
		name           string
		roomID         int64
		userID         int64
		messageIDs     []int64
		setupMessages  map[int64]*entity.MessageEntity
		authorizedRoom bool
		expectError    bool
		expectedCount  int // Expected number of messages to be updated
	}{
		{
			name:       "valid bulk delivery update",
			roomID:     100,
			userID:     2,
			messageIDs: []int64{1, 2, 3},
			setupMessages: map[int64]*entity.MessageEntity{
				1: {
					BaseEntity: entity.BaseEntity{ID: 1},
					ChatRoomID: 100,
					SenderID:   func() *int64 { id := int64(1); return &id }(),
					Content:    "Message 1",
					Status:     constant.MessageStatusSent,
				},
				2: {
					BaseEntity: entity.BaseEntity{ID: 2},
					ChatRoomID: 100,
					SenderID:   func() *int64 { id := int64(3); return &id }(),
					Content:    "Message 2",
					Status:     constant.MessageStatusSent,
				},
				3: {
					BaseEntity: entity.BaseEntity{ID: 3},
					ChatRoomID: 100,
					SenderID:   func() *int64 { id := int64(1); return &id }(),
					Content:    "Message 3",
					Status:     constant.MessageStatusSent,
				},
			},
			authorizedRoom: true,
			expectError:    false,
			expectedCount:  3, // All 3 messages should be updated
		},
		{
			name:       "unauthorized user",
			roomID:     100,
			userID:     3,
			messageIDs: []int64{1, 2, 3},
			setupMessages: map[int64]*entity.MessageEntity{
				1: {
					BaseEntity: entity.BaseEntity{ID: 1},
					ChatRoomID: 100,
					SenderID:   func() *int64 { id := int64(1); return &id }(),
					Content:    "Message 1",
					Status:     constant.MessageStatusSent,
				},
			},
			authorizedRoom: false,
			expectError:    true,
			expectedCount:  0,
		},
		{
			name:           "empty message list",
			roomID:         100,
			userID:         2,
			messageIDs:     []int64{},
			setupMessages:  map[int64]*entity.MessageEntity{},
			authorizedRoom: true,
			expectError:    false,
			expectedCount:  0,
		},
		{
			name:       "skip messages from same user",
			roomID:     100,
			userID:     2,
			messageIDs: []int64{1, 2},
			setupMessages: map[int64]*entity.MessageEntity{
				1: {
					BaseEntity: entity.BaseEntity{ID: 1},
					ChatRoomID: 100,
					SenderID:   func() *int64 { id := int64(2); return &id }(), // Same as userID
					Content:    "Message 1",
					Status:     constant.MessageStatusSent,
				},
				2: {
					BaseEntity: entity.BaseEntity{ID: 2},
					ChatRoomID: 100,
					SenderID:   func() *int64 { id := int64(1); return &id }(), // Different from userID
					Content:    "Message 2",
					Status:     constant.MessageStatusSent,
				},
			},
			authorizedRoom: true,
			expectError:    false,
			expectedCount:  1, // Only message 2 should be updated (not from same user)
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			// Setup mocks
			mockMessagePort := &mockMessagePort{
				messages: tt.setupMessages,
			}

			mockChatRoomUseCase := &mockGetChatRoomUseCase{
				authorizedRooms: make(map[int64][]int64),
			}

			if tt.authorizedRoom {
				mockChatRoomUseCase.authorizedRooms[tt.roomID] = []int64{tt.userID}
			}

			mockDBUseCase := &mockDatabaseTransactionUseCase{}

			// Create use case
			useCase := NewUpdateMessageUseCase(
				mockMessagePort,
				mockChatRoomUseCase,
				mockDBUseCase,
			)

			// Set the userID context for the mock (for tracking purposes)
			mockMessagePort.SetUpdateUserID(tt.userID)

			// Execute
			err := useCase.MarkMessagesAsDelivered(context.Background(), tt.roomID, tt.userID, tt.messageIDs)

			// Verify
			if tt.expectError {
				if err == nil {
					t.Errorf("expected error but got none")
				}
				return
			}

			if err != nil {
				t.Errorf("unexpected error: %v", err)
				return
			}

			// Verify the correct number of messages were updated
			if len(mockMessagePort.statusUpdates) != tt.expectedCount {
				t.Errorf("expected %d status updates but got %d", tt.expectedCount, len(mockMessagePort.statusUpdates))
				return
			}

			// For each updated message, verify it was set to delivered status
			for _, update := range mockMessagePort.statusUpdates {
				if update.status != constant.MessageStatusDelivered {
					t.Errorf("expected message status to be delivered but got %s", update.status)
				}
			}
		})
	}
}

func TestUpdateMessageUseCase_MarkMessageAsRead(t *testing.T) {
	// Setup test message
	messageID := int64(1)
	roomID := int64(100)
	userID := int64(2)

	mockMessagePort := &mockMessagePort{messages: map[int64]*entity.MessageEntity{
		messageID: {
			BaseEntity: entity.BaseEntity{
				ID: messageID,
			},
			ChatRoomID: roomID,
			SenderID:   func() *int64 { id := int64(3); return &id }(),
			Content:    "Hello",
			Status:     constant.MessageStatusDelivered,
		},
	},
	}

	mockChatRoomUseCase := &mockGetChatRoomUseCase{
		authorizedRooms: map[int64][]int64{
			roomID: {userID},
		},
	}

	mockDBUseCase := &mockDatabaseTransactionUseCase{}

	// Create use case
	useCase := NewUpdateMessageUseCase(
		mockMessagePort,
		mockChatRoomUseCase,
		mockDBUseCase,
	)

	// Set the userID context for the mock (for tracking purposes)
	mockMessagePort.SetUpdateUserID(userID)

	// Execute
	err := useCase.MarkMessageAsRead(context.Background(), roomID, userID, messageID)

	// Verify
	if err != nil {
		t.Errorf("unexpected error: %v", err)
		return
	}

	// Verify status update was called with read status
	if len(mockMessagePort.statusUpdates) != 1 {
		t.Errorf("expected 1 status update but got %d", len(mockMessagePort.statusUpdates))
		return
	}

	update := mockMessagePort.statusUpdates[0]
	if update.messageID != messageID || update.status != constant.MessageStatusRead || update.userID != userID {
		t.Errorf("status update mismatch: got %+v", update)
	}
}
