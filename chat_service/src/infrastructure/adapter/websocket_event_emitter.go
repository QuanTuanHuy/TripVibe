package adapter

import (
	"chat_service/core/domain/constant"
	"chat_service/core/domain/ws"
	"chat_service/infrastructure"
	"context"
	"github.com/golibs-starter/golib/log"
	"time"
)

// WebSocketEventEmitter implements the IEventEmitter interface for real-time status updates
type WebSocketEventEmitter struct {
	wsManager *infrastructure.WebSocketManager
}

// NewWebSocketEventEmitter creates a new WebSocket event emitter
func NewWebSocketEventEmitter(wsManager *infrastructure.WebSocketManager) *WebSocketEventEmitter {
	return &WebSocketEventEmitter{
		wsManager: wsManager,
	}
}

// EmitMessageStatusChange broadcasts message status changes to all users in the room
func (e *WebSocketEventEmitter) EmitMessageStatusChange(
	ctx context.Context,
	roomID int64,
	messageID int64,
	oldStatus, newStatus constant.MessageStatus,
	updatedBy int64,
) error {
	log.Info(ctx, "Emitting message status change event", 
		"roomID", roomID, "messageID", messageID, 
		"oldStatus", oldStatus, "newStatus", newStatus, "updatedBy", updatedBy)

	// Create status update payload
	payload := &ws.StatusUpdatePayload{
		MessageID: messageID,
		OldStatus: oldStatus,
		NewStatus: newStatus,
		UpdatedBy: updatedBy,
		UpdatedAt: time.Now().Unix(),
		RoomID:    roomID,
	}

	// Create WebSocket message
	message := &ws.WebSocketMessage{
		Type:    ws.MessageTypeStatusUpdate,
		Payload: payload,
	}
	// Broadcast to all users in the room
	err := e.wsManager.BroadcastToRoomWithError(roomID, message)
	if err != nil {
		log.Error(ctx, "Failed to broadcast status change", "error", err, "roomID", roomID)
		return err
	}

	log.Info(ctx, "Successfully emitted message status change event", 
		"roomID", roomID, "messageID", messageID)
	return nil
}

// EmitAcknowledgment sends an acknowledgment message to a specific user
func (e *WebSocketEventEmitter) EmitAcknowledgment(
	ctx context.Context,
	userID int64,
	messageType string,
	messageID *int64,
	status string,
	errorMsg string,
) error {
	payload := &ws.AcknowledgmentPayload{
		MessageType: messageType,
		MessageID:   messageID,
		Status:      status,
		Error:       errorMsg,
	}

	message := &ws.WebSocketMessage{
		Type:    ws.MessageTypeAcknowledgment,
		Payload: payload,
	}

	return e.wsManager.SendToUser(userID, message)
}
