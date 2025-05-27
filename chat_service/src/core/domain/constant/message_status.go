package constant

// MessageStatus represents the delivery status of a message
type MessageStatus string

const (
	// MessageStatusSent indicates the message has been sent but not yet delivered
	MessageStatusSent MessageStatus = "sent"

	// MessageStatusDelivered indicates the message has been delivered to the recipient
	MessageStatusDelivered MessageStatus = "delivered"

	// MessageStatusRead indicates the message has been read by the recipient
	MessageStatusRead MessageStatus = "read"
)

// IsValid checks if the message status is valid
func (ms MessageStatus) IsValid() bool {
	switch ms {
	case MessageStatusSent, MessageStatusDelivered, MessageStatusRead:
		return true
	default:
		return false
	}
}

// CanTransitionTo checks if a status transition is valid
func (ms MessageStatus) CanTransitionTo(newStatus MessageStatus) bool {
	switch ms {
	case MessageStatusSent:
		return newStatus == MessageStatusDelivered || newStatus == MessageStatusRead
	case MessageStatusDelivered:
		return newStatus == MessageStatusRead
	case MessageStatusRead:
		return false // Cannot transition from read to any other status
	default:
		return false
	}
}
