package entity

import (
	"chat_service/core/domain/constant"
	"errors"
	"time"
)

type MessageEntity struct {
	BaseEntity
	ChatRoomID  int64                  `json:"chatRoomId"`
	SenderID    *int64                 `json:"senderId"`
	Content     string                 `json:"content"`
	Type        constant.MessageType   `json:"type"`
	IsRead      bool                   `json:"isRead"`      // Deprecated: Use Status field instead
	Status      constant.MessageStatus `json:"status"`      // New: Message delivery status
	DeliveredAt *int64                 `json:"deliveredAt"` // New: Timestamp when message was delivered
	ReadAt      *int64                 `json:"readAt"`      // New: Timestamp when message was read
}

// Validate checks if the message entity is valid
func (m *MessageEntity) Validate() error {
	if m.ChatRoomID <= 0 {
		return errors.New("chat room ID must be positive")
	}

	if m.Content == "" && m.Type == constant.TextMessage {
		return errors.New("text messages must have content")
	}

	if m.Status != "" && !m.Status.IsValid() {
		return errors.New("invalid message status")
	}

	// Validate timestamp consistency
	if m.DeliveredAt != nil && m.ReadAt != nil {
		if *m.DeliveredAt > *m.ReadAt {
			return errors.New("delivered timestamp cannot be after read timestamp")
		}
	}

	if m.CreatedAt > 0 {
		if m.DeliveredAt != nil && m.CreatedAt > *m.DeliveredAt {
			return errors.New("created timestamp cannot be after delivered timestamp")
		}
		if m.ReadAt != nil && m.CreatedAt > *m.ReadAt {
			return errors.New("created timestamp cannot be after read timestamp")
		}
	}

	return nil
}

// UpdateStatus updates the message status with proper validation and timestamp handling
func (m *MessageEntity) UpdateStatus(newStatus constant.MessageStatus) error {
	// If no current status is set, allow setting to any valid status
	if m.Status == "" {
		if !newStatus.IsValid() {
			return errors.New("invalid message status")
		}
		m.Status = newStatus
		m.setStatusTimestamp(newStatus)
		m.syncIsReadField() // Maintain backward compatibility
		return nil
	}

	// Validate status transition
	if !m.Status.CanTransitionTo(newStatus) {
		return errors.New("invalid status transition from " + string(m.Status) + " to " + string(newStatus))
	}

	m.Status = newStatus
	m.setStatusTimestamp(newStatus)
	m.syncIsReadField() // Maintain backward compatibility

	return nil
}

// setStatusTimestamp sets the appropriate timestamp based on status
func (m *MessageEntity) setStatusTimestamp(status constant.MessageStatus) {
	now := time.Now().Unix()

	switch status {
	case constant.MessageStatusDelivered:
		if m.DeliveredAt == nil {
			m.DeliveredAt = &now
		}
	case constant.MessageStatusRead:
		if m.DeliveredAt == nil {
			m.DeliveredAt = &now
		}
		if m.ReadAt == nil {
			m.ReadAt = &now
		}
	}
}

// syncIsReadField maintains backward compatibility with the deprecated IsRead field
func (m *MessageEntity) syncIsReadField() {
	m.IsRead = m.Status == constant.MessageStatusRead
}

// GetStatus returns the current status, with fallback to IsRead field for backward compatibility
func (m *MessageEntity) GetStatus() constant.MessageStatus {
	if m.Status != "" {
		return m.Status
	}

	// Backward compatibility: infer status from IsRead field
	if m.IsRead {
		return constant.MessageStatusRead
	}

	return constant.MessageStatusSent
}

// IsDelivered checks if the message has been delivered
func (m *MessageEntity) IsDelivered() bool {
	status := m.GetStatus()
	return status == constant.MessageStatusDelivered || status == constant.MessageStatusRead
}

// IsMessageRead checks if the message has been read
func (m *MessageEntity) IsMessageRead() bool {
	return m.GetStatus() == constant.MessageStatusRead
}
