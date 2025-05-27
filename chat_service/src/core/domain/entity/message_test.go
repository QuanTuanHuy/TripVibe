package entity

import (
	"chat_service/core/domain/constant"
	"testing"
	"time"
)

func TestMessageEntity_Validate(t *testing.T) {
	tests := []struct {
		name    string
		message MessageEntity
		wantErr bool
	}{
		{
			name: "valid text message",
			message: MessageEntity{
				ChatRoomID: 1,
				Content:    "Hello",
				Type:       constant.TextMessage,
				Status:     constant.MessageStatusSent,
			},
			wantErr: false,
		},
		{
			name: "invalid chat room ID",
			message: MessageEntity{
				ChatRoomID: 0,
				Content:    "Hello",
				Type:       constant.TextMessage,
			},
			wantErr: true,
		},
		{
			name: "empty content for text message",
			message: MessageEntity{
				ChatRoomID: 1,
				Content:    "",
				Type:       constant.TextMessage,
			},
			wantErr: true,
		},
		{
			name: "invalid status",
			message: MessageEntity{
				ChatRoomID: 1,
				Content:    "Hello",
				Type:       constant.TextMessage,
				Status:     "invalid",
			},
			wantErr: true,
		},
		{
			name: "delivered after read timestamp",
			message: MessageEntity{
				ChatRoomID:  1,
				Content:     "Hello",
				Type:        constant.TextMessage,
				Status:      constant.MessageStatusRead,
				DeliveredAt: &[]int64{time.Now().Unix()}[0],
				ReadAt:      &[]int64{time.Now().Unix() - 100}[0],
			},
			wantErr: true,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			err := tt.message.Validate()
			if (err != nil) != tt.wantErr {
				t.Errorf("MessageEntity.Validate() error = %v, wantErr %v", err, tt.wantErr)
			}
		})
	}
}

func TestMessageEntity_UpdateStatus(t *testing.T) {
	tests := []struct {
		name       string
		message    MessageEntity
		newStatus  constant.MessageStatus
		wantErr    bool
		wantIsRead bool
	}{
		{
			name: "transition from sent to delivered",
			message: MessageEntity{
				Status: constant.MessageStatusSent,
			},
			newStatus:  constant.MessageStatusDelivered,
			wantErr:    false,
			wantIsRead: false,
		},
		{
			name: "transition from sent to read",
			message: MessageEntity{
				Status: constant.MessageStatusSent,
			},
			newStatus:  constant.MessageStatusRead,
			wantErr:    false,
			wantIsRead: true,
		},
		{
			name: "transition from delivered to read",
			message: MessageEntity{
				Status: constant.MessageStatusDelivered,
			},
			newStatus:  constant.MessageStatusRead,
			wantErr:    false,
			wantIsRead: true,
		},
		{
			name: "invalid transition from read to delivered",
			message: MessageEntity{
				Status: constant.MessageStatusRead,
			},
			newStatus: constant.MessageStatusDelivered,
			wantErr:   true,
		},
		{
			name: "set initial status to sent",
			message: MessageEntity{
				Status: "",
			},
			newStatus:  constant.MessageStatusSent,
			wantErr:    false,
			wantIsRead: false,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			err := tt.message.UpdateStatus(tt.newStatus)
			if (err != nil) != tt.wantErr {
				t.Errorf("MessageEntity.UpdateStatus() error = %v, wantErr %v", err, tt.wantErr)
				return
			}

			if !tt.wantErr {
				if tt.message.Status != tt.newStatus {
					t.Errorf("MessageEntity.UpdateStatus() status = %v, want %v", tt.message.Status, tt.newStatus)
				}
				if tt.message.IsRead != tt.wantIsRead {
					t.Errorf("MessageEntity.UpdateStatus() IsRead = %v, want %v", tt.message.IsRead, tt.wantIsRead)
				}
			}
		})
	}
}

func TestMessageEntity_GetStatus(t *testing.T) {
	tests := []struct {
		name     string
		message  MessageEntity
		expected constant.MessageStatus
	}{
		{
			name: "status field set",
			message: MessageEntity{
				Status: constant.MessageStatusDelivered,
				IsRead: false,
			},
			expected: constant.MessageStatusDelivered,
		},
		{
			name: "backward compatibility - IsRead true",
			message: MessageEntity{
				Status: "",
				IsRead: true,
			},
			expected: constant.MessageStatusRead,
		},
		{
			name: "backward compatibility - IsRead false",
			message: MessageEntity{
				Status: "",
				IsRead: false,
			},
			expected: constant.MessageStatusSent,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			result := tt.message.GetStatus()
			if result != tt.expected {
				t.Errorf("MessageEntity.GetStatus() = %v, want %v", result, tt.expected)
			}
		})
	}
}

func TestMessageEntity_IsDelivered(t *testing.T) {
	tests := []struct {
		name     string
		message  MessageEntity
		expected bool
	}{
		{
			name: "status delivered",
			message: MessageEntity{
				Status: constant.MessageStatusDelivered,
			},
			expected: true,
		},
		{
			name: "status read",
			message: MessageEntity{
				Status: constant.MessageStatusRead,
			},
			expected: true,
		},
		{
			name: "status sent",
			message: MessageEntity{
				Status: constant.MessageStatusSent,
			},
			expected: false,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			result := tt.message.IsDelivered()
			if result != tt.expected {
				t.Errorf("MessageEntity.IsDelivered() = %v, want %v", result, tt.expected)
			}
		})
	}
}

func TestMessageEntity_IsMessageRead(t *testing.T) {
	tests := []struct {
		name     string
		message  MessageEntity
		expected bool
	}{
		{
			name: "status read",
			message: MessageEntity{
				Status: constant.MessageStatusRead,
			},
			expected: true,
		},
		{
			name: "status delivered",
			message: MessageEntity{
				Status: constant.MessageStatusDelivered,
			},
			expected: false,
		},
		{
			name: "backward compatibility - IsRead true",
			message: MessageEntity{
				Status: "",
				IsRead: true,
			},
			expected: true,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			result := tt.message.IsMessageRead()
			if result != tt.expected {
				t.Errorf("MessageEntity.IsMessageRead() = %v, want %v", result, tt.expected)
			}
		})
	}
}

func TestMessageEntity_UpdateStatus_SetsTimestamps(t *testing.T) {
	message := MessageEntity{
		Status: constant.MessageStatusSent,
	}

	// Test setting to delivered
	err := message.UpdateStatus(constant.MessageStatusDelivered)
	if err != nil {
		t.Errorf("UpdateStatus() error = %v", err)
	}

	if message.DeliveredAt == nil {
		t.Error("DeliveredAt should be set when status changes to delivered")
	}

	if message.ReadAt != nil {
		t.Error("ReadAt should not be set when status is delivered")
	}

	// Test setting to read
	err = message.UpdateStatus(constant.MessageStatusRead)
	if err != nil {
		t.Errorf("UpdateStatus() error = %v", err)
	}

	if message.ReadAt == nil {
		t.Error("ReadAt should be set when status changes to read")
	}

	if message.DeliveredAt == nil {
		t.Error("DeliveredAt should remain set when status changes to read")
	}
}
