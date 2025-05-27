package usecase

import (
	"chat_service/core/domain/constant"
	"chat_service/core/domain/dto/request"
	"chat_service/core/domain/entity"
	"chat_service/core/port"
	"context"
	"errors"

	"github.com/golibs-starter/golib/log"
)

type IUpdateMessageUseCase interface {
	MarkMessageAsRead(ctx context.Context, chatRoomID, userID, messageID int64) error
	// New status-related methods for Task 1.2.4
	UpdateMessageStatus(ctx context.Context, messageID int64, status constant.MessageStatus, userID int64) (*entity.MessageEntity, error)
	MarkMessagesAsDelivered(ctx context.Context, roomID, userID int64, messageIDs []int64) error
	BulkUpdateMessageStatus(ctx context.Context, roomID int64, fromStatus, toStatus constant.MessageStatus, userID int64) ([]int64, error)
}

type UpdateMessageUseCase struct {
	messagePort          port.IMessagePort
	getChatRoomUseCase   IGetChatRoomUseCase
	dbTransactionUseCase IDatabaseTransactionUseCase
	// Event emission interface - will be added when WebSocket events are implemented
	eventEmitter IEventEmitter
}

// IEventEmitter defines the interface for emitting status change events
type IEventEmitter interface {
	EmitMessageStatusChange(ctx context.Context, roomID int64, messageID int64, oldStatus, newStatus constant.MessageStatus, updatedBy int64) error
}

// UpdateMessageStatus updates the status of a specific message with validation and event emission
func (u *UpdateMessageUseCase) UpdateMessageStatus(ctx context.Context, messageID int64, status constant.MessageStatus, userID int64) (*entity.MessageEntity, error) {
	// Validate the status is valid
	if !status.IsValid() {
		log.Error(ctx, "Invalid message status: ", status)
		return nil, errors.New("invalid message status")
	}

	// Get the message to validate authorization and current status
	messages, err := u.messagePort.GetMessagesByIDs(ctx, []int64{messageID})
	if err != nil {
		log.Error(ctx, "Failed to get message for status update: ", err)
		return nil, err
	}

	if len(messages) == 0 {
		log.Error(ctx, "Message not found: ", messageID)
		return nil, errors.New("message not found")
	}

	message := messages[0]

	// Check permission - user must have access to the chat room
	_, err = u.getChatRoomUseCase.GetChatRoomById(ctx, message.ChatRoomID, userID)
	if err != nil {
		log.Error(ctx, "User does not have access to chat room: ", err)
		return nil, errors.New("unauthorized to update message status")
	}

	// Prevent users from updating their own message status (except for read status)
	if message.SenderID != nil && *message.SenderID == userID && status != constant.MessageStatusRead {
		log.Error(ctx, "Users cannot update status of their own messages (except read): ", messageID)
		return nil, errors.New("cannot update status of own message")
	}

	// Store old status for event emission
	oldStatus := message.GetStatus()

	// Use MessageEntity to validate and update status
	err = message.UpdateStatus(status)
	if err != nil {
		log.Error(ctx, "Failed to update message status: ", err)
		return nil, err
	}

	// Start transaction
	tx := u.dbTransactionUseCase.StartTransaction()
	defer func() {
		if err := u.dbTransactionUseCase.Rollback(tx); err != nil {
			log.Error(ctx, "Rollback update message status failed: ", err)
		}
	}()

	// Update the entire message in database
	err = u.messagePort.UpdateMessage(ctx, tx, message)
	if err != nil {
		log.Error(ctx, "Failed to update message in database: ", err)
		return nil, err
	}

	// Commit transaction
	err = u.dbTransactionUseCase.Commit(tx)
	if err != nil {
		log.Error(ctx, "Failed to commit message status update: ", err)
		return nil, err
	}

	// Emit status change event (if event emitter is available)
	if u.eventEmitter != nil {
		err = u.eventEmitter.EmitMessageStatusChange(ctx, message.ChatRoomID, messageID, oldStatus, status, userID)
		if err != nil {
			log.Error(ctx, "Failed to emit status change event: ", err)
			// Don't fail the operation for event emission failures
		}
	}

	log.Info(ctx, "Successfully updated message status: messageID=", messageID, " oldStatus=", oldStatus, " newStatus=", status, " userID=", userID)
	return message, nil
}

// MarkMessagesAsDelivered bulk operation to mark multiple messages as delivered
func (u *UpdateMessageUseCase) MarkMessagesAsDelivered(ctx context.Context, roomID, userID int64, messageIDs []int64) error {
	if len(messageIDs) == 0 {
		return nil // Nothing to update
	}

	// Check permission - user must have access to the chat room
	_, err := u.getChatRoomUseCase.GetChatRoomById(ctx, roomID, userID)
	if err != nil {
		log.Error(ctx, "User does not have access to chat room: ", err)
		return errors.New("unauthorized to mark messages as delivered")
	}

	// Get the messages to validate and update using business logic
	messages, err := u.messagePort.GetMessagesByIDs(ctx, messageIDs)
	if err != nil {
		log.Error(ctx, "Failed to get messages for delivered status update: ", err)
		return err
	}

	if len(messages) == 0 {
		log.Info(ctx, "No messages found to mark as delivered")
		return nil
	}

	var updatedMessages []*entity.MessageEntity
	var oldStatuses []constant.MessageStatus

	// Validate and update each message using entity business logic
	for _, message := range messages {
		// Skip messages sent by the same user (they shouldn't mark their own messages as delivered)
		if message.SenderID != nil && *message.SenderID == userID {
			continue
		}

		// Skip messages that are not in the correct room
		if message.ChatRoomID != roomID {
			continue
		}

		// Store old status for event emission
		oldStatus := message.GetStatus()

		// Use MessageEntity to validate and update status
		err = message.UpdateStatus(constant.MessageStatusDelivered)
		if err != nil {
			log.Error(ctx, "Failed to validate status transition for message: messageID=", message.ID, " error=", err)
			continue
		}

		updatedMessages = append(updatedMessages, message)
		oldStatuses = append(oldStatuses, oldStatus)
	}

	if len(updatedMessages) == 0 {
		log.Info(ctx, "No messages needed status update to delivered")
		return nil
	}

	// Start transaction
	tx := u.dbTransactionUseCase.StartTransaction()
	defer func() {
		if err := u.dbTransactionUseCase.Rollback(tx); err != nil {
			log.Error(ctx, "Rollback mark messages as delivered failed: ", err)
		}
	}()

	// Update each message in database
	for _, message := range updatedMessages {
		err = u.messagePort.UpdateMessage(ctx, tx, message)
		if err != nil {
			log.Error(ctx, "Failed to update message as delivered: messageID=", message.ID, " error=", err)
			return err
		}
	}

	// Commit transaction
	err = u.dbTransactionUseCase.Commit(tx)
	if err != nil {
		log.Error(ctx, "Failed to commit mark messages as delivered: ", err)
		return err
	}

	// Emit events for each updated message (if event emitter is available)
	if u.eventEmitter != nil {
		for i, message := range updatedMessages {
			err = u.eventEmitter.EmitMessageStatusChange(ctx, roomID, message.ID, oldStatuses[i], constant.MessageStatusDelivered, userID)
			if err != nil {
				log.Error(ctx, "Failed to emit delivered status event for message: ", message.ID, " error: ", err)
				// Continue with other messages
			}
		}
	}

	log.Info(ctx, "Successfully marked messages as delivered: roomID=", roomID, " userID=", userID, " count=", len(updatedMessages))
	return nil
}

// BulkUpdateMessageStatus updates multiple messages from one status to another
func (u *UpdateMessageUseCase) BulkUpdateMessageStatus(ctx context.Context, roomID int64, fromStatus, toStatus constant.MessageStatus, userID int64) ([]int64, error) {
	// Validate statuses
	if !fromStatus.IsValid() || !toStatus.IsValid() {
		log.Error(ctx, "Invalid status provided: fromStatus=", fromStatus, " toStatus=", toStatus)
		return nil, errors.New("invalid status provided")
	}

	// Validate status transition
	if !fromStatus.CanTransitionTo(toStatus) {
		log.Error(ctx, "Invalid status transition from ", fromStatus, " to ", toStatus)
		return nil, errors.New("invalid status transition")
	}

	// Check permission - user must have access to the chat room
	_, err := u.getChatRoomUseCase.GetChatRoomById(ctx, roomID, userID)
	if err != nil {
		log.Error(ctx, "User does not have access to chat room: ", err)
		return nil, errors.New("unauthorized to update message statuses")
	}
	// Get messages with the specified status
	params := &request.MessageQueryParams{
		Limit: 1000, // Reasonable batch size
	}

	messages, _, err := u.messagePort.GetMessagesByStatus(ctx, roomID, fromStatus, params)
	if err != nil {
		log.Error(ctx, "Failed to get messages by status: ", err)
		return nil, err
	}

	if len(messages) == 0 {
		log.Info(ctx, "No messages found with status ", fromStatus, " in room ", roomID)
		return []int64{}, nil
	}

	var updatedMessageIDs []int64

	// Start transaction
	tx := u.dbTransactionUseCase.StartTransaction()
	defer func() {
		if err := u.dbTransactionUseCase.Rollback(tx); err != nil {
			log.Error(ctx, "Rollback bulk update message status failed: ", err)
		}
	}()

	// Update each message
	for _, message := range messages {
		// Skip messages sent by the same user (they shouldn't update their own message status)
		if message.SenderID != nil && *message.SenderID == userID && toStatus != constant.MessageStatusRead {
			continue
		}

		// Use MessageEntity to validate and update status
		err = message.UpdateStatus(toStatus)
		if err != nil {
			log.Error(ctx, "Failed to validate status transition for message: messageID=", message.ID, " error=", err)
			continue // Continue with other messages
		}

		// Update the entire message in database
		err = u.messagePort.UpdateMessage(ctx, tx, message)
		if err != nil {
			log.Error(ctx, "Failed to update message in bulk: messageID=", message.ID, " error=", err)
			continue // Continue with other messages
		}

		updatedMessageIDs = append(updatedMessageIDs, message.ID)
	}

	// Commit transaction
	err = u.dbTransactionUseCase.Commit(tx)
	if err != nil {
		log.Error(ctx, "Failed to commit bulk message status update: ", err)
		return nil, err
	}

	// Emit events for updated messages (if event emitter is available)
	if u.eventEmitter != nil {
		for _, messageID := range updatedMessageIDs {
			err = u.eventEmitter.EmitMessageStatusChange(ctx, roomID, messageID, fromStatus, toStatus, userID)
			if err != nil {
				log.Error(ctx, "Failed to emit status change event for message: ", messageID, " error: ", err)
				// Continue with other messages
			}
		}
	}

	log.Info(ctx, "Successfully updated message statuses in bulk: roomID=", roomID, " fromStatus=", fromStatus, " toStatus=", toStatus, " userID=", userID, " count=", len(updatedMessageIDs))
	return updatedMessageIDs, nil
}

// MarkMessageAsRead marks a message as read (existing method)
func (u *UpdateMessageUseCase) MarkMessageAsRead(ctx context.Context, roomID, userID, messageID int64) error {
	// Use the new UpdateMessageStatus method for consistency
	_, err := u.UpdateMessageStatus(ctx, messageID, constant.MessageStatusRead, userID)
	return err
}

func NewUpdateMessageUseCase(messagePort port.IMessagePort,
	getChatRoomUseCase IGetChatRoomUseCase,
	dbTransactionUseCase IDatabaseTransactionUseCase) IUpdateMessageUseCase {
	return &UpdateMessageUseCase{
		messagePort:          messagePort,
		getChatRoomUseCase:   getChatRoomUseCase,
		dbTransactionUseCase: dbTransactionUseCase,
		// eventEmitter will be injected later when WebSocket events are implemented
		eventEmitter: nil,
	}
}

// NewUpdateMessageUseCaseWithEvents creates a use case with event emission capability
func NewUpdateMessageUseCaseWithEvents(messagePort port.IMessagePort,
	getChatRoomUseCase IGetChatRoomUseCase,
	dbTransactionUseCase IDatabaseTransactionUseCase,
	eventEmitter IEventEmitter) IUpdateMessageUseCase {
	return &UpdateMessageUseCase{
		messagePort:          messagePort,
		getChatRoomUseCase:   getChatRoomUseCase,
		dbTransactionUseCase: dbTransactionUseCase,
		eventEmitter:         eventEmitter,
	}
}
