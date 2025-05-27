package adapter

import (
	"chat_service/core/domain/constant"
	"chat_service/core/domain/dto/request"
	"chat_service/core/domain/dto/response"
	"chat_service/core/domain/entity"
	"chat_service/core/port"
	"chat_service/infrastructure/repository/mapper"
	"chat_service/infrastructure/repository/model"
	"context"
	"errors"

	"github.com/golibs-starter/golib/log"
	"gorm.io/gorm"
)

type MessageAdapter struct {
	base
}

func (m MessageAdapter) CountUnreadMessages(ctx context.Context, roomID, userID int64) (int64, error) {
	var count int64
	if err := m.db.WithContext(ctx).
		Model(model.MessageModel{}).
		Where("chat_room_id = ? AND sender_id != ? AND is_read = false", roomID, userID).
		Count(&count).Error; err != nil {
		log.Error(ctx, "Error counting unread messages: ", err)
		return 0, err
	}
	return count, nil
}

func (m MessageAdapter) MarkMessageAsRead(ctx context.Context, tx *gorm.DB, roomID, userID, messageID int64) error {
	result := tx.WithContext(ctx).
		Exec("UPDATE messages SET is_read = true WHERE id <= ? AND chat_room_id = ? AND sender_id != ?",
			messageID, roomID, userID)

	if result.Error != nil {
		log.Error(ctx, "Error marking message as read: ", result.Error)
		return result.Error
	}
	return nil
}

func (m MessageAdapter) GetMessagesByIDs(ctx context.Context, messageIDs []int64) ([]*entity.MessageEntity, error) {
	if len(messageIDs) == 1 {
		var messageModel model.MessageModel
		if err := m.db.WithContext(ctx).
			Where("id = ?", messageIDs[0]).
			First(&messageModel).Error; err != nil {
			return nil, err
		}
		return []*entity.MessageEntity{mapper.ToMessageEntity(&messageModel)}, nil
	}

	var messageModels []*model.MessageModel
	if err := m.db.WithContext(ctx).
		Where("id IN (?)", messageIDs).
		Find(&messageModels).Error; err != nil {
		return nil, err
	}
	return mapper.ToMessageEntities(messageModels), nil
}

func (m MessageAdapter) GetMessagesByRoomId(ctx context.Context, chatRoomID int64, params *request.MessageQueryParams) ([]*entity.MessageEntity, *response.PaginationResult, error) {
	var messageModels []*model.MessageModel

	limit := constant.DefaultLimitMessage
	if params.Limit > 0 {
		limit = params.Limit
	}
	if limit > constant.MaxLimitMessage {
		limit = constant.MaxLimitMessage
	}

	query := m.db.WithContext(ctx).
		Where("chat_room_id = ?", chatRoomID).
		Limit(limit + 1)

	if params.SenderID != nil {
		query = query.Where("sender_id = ?", *params.SenderID)
	}

	if params.MessageType != nil {
		query = query.Where("type = ?", *params.MessageType)
	}

	// Cursor-based pagination
	direction := constant.OlderDirection
	if params.Direction == constant.NewerDirection {
		direction = constant.NewerDirection
	}

	if params.Cursor != nil {
		if direction == constant.OlderDirection {
			query = query.Where("id < ?", *params.Cursor).Order("id DESC")
		} else {
			query = query.Where("id > ?", *params.Cursor).Order("id ASC")
		}
	} else {
		query = query.Order("id DESC")
	}

	result := query.Find(&messageModels)
	if result.Error != nil {
		return nil, nil, result.Error
	}

	hasMore := false
	if len(messageModels) > limit {
		hasMore = true
		messageModels = messageModels[:limit]
	}

	paginationResult := &response.PaginationResult{
		HasMore: hasMore,
	}

	// Đảo ngược kết quả nếu đang lấy tin nhắn mới hơn để có thứ tự thời gian tăng dần
	if direction == constant.NewerDirection && len(messageModels) > 0 {
		// Đảo ngược mảng
		for i, j := 0, len(messageModels)-1; i < j; i, j = i+1, j-1 {
			messageModels[i], messageModels[j] = messageModels[j], messageModels[i]
		}
	}

	// Thiết lập cursor cho lần tải tiếp theo
	if len(messageModels) > 0 {
		// Cursor tiếp theo là ID của tin nhắn cuối cùng
		lastID := messageModels[len(messageModels)-1].ID
		paginationResult.NextCursor = &lastID

		// Cursor trước đó là ID của tin nhắn đầu tiên
		firstID := messageModels[0].ID
		paginationResult.PreviousCursor = &firstID

	}

	// Ước tính tổng số tin nhắn (chỉ để hiển thị UI, không chính xác hoàn toàn)
	var count int64
	m.db.WithContext(ctx).Model(&model.MessageModel{}).Where("chat_room_id = ?", chatRoomID).Count(&count)
	paginationResult.TotalItems = count

	return mapper.ToMessageEntities(messageModels), paginationResult, nil
}

func (m MessageAdapter) CreateMessage(ctx context.Context, tx *gorm.DB, message *entity.MessageEntity) (*entity.MessageEntity, error) {
	messageModel := mapper.ToMessageModel(message)
	if err := tx.WithContext(ctx).Create(messageModel).Error; err != nil {
		return nil, err
	}
	return mapper.ToMessageEntity(messageModel), nil
}

// UpdateMessage updates an entire message entity in the database
func (m MessageAdapter) UpdateMessage(ctx context.Context, tx *gorm.DB, message *entity.MessageEntity) error {
	// Convert entity to model for database update
	messageModel := mapper.ToMessageModel(message)

	// Update the entire message
	result := tx.WithContext(ctx).Model(&model.MessageModel{}).
		Where("id = ?", message.ID).
		Updates(messageModel)

	if result.Error != nil {
		log.Error(ctx, "Error updating message: ", result.Error)
		return result.Error
	}

	if result.RowsAffected == 0 {
		log.Error(ctx, "No message found with ID: ", message.ID)
		return errors.New("message not found")
	}

	return nil
}

// GetUnreadMessages retrieves unread messages for a user in a specific room with pagination
func (m MessageAdapter) GetUnreadMessages(ctx context.Context, roomID, userID int64, params *request.MessageQueryParams) ([]*entity.MessageEntity, *response.PaginationResult, error) {
	var messageModels []*model.MessageModel

	limit := constant.DefaultLimitMessage
	if params.Limit > 0 {
		limit = params.Limit
	}
	if limit > constant.MaxLimitMessage {
		limit = constant.MaxLimitMessage
	}

	query := m.db.WithContext(ctx).
		Where("chat_room_id = ? AND sender_id != ?", roomID, userID).
		Where("status IN (?, ?) OR (status IS NULL AND is_read = false)",
			constant.MessageStatusSent, constant.MessageStatusDelivered).
		Limit(limit + 1)

	// Cursor-based pagination
	direction := constant.OlderDirection
	if params.Direction == constant.NewerDirection {
		direction = constant.NewerDirection
	}

	if params.Cursor != nil {
		if direction == constant.OlderDirection {
			query = query.Where("id < ?", *params.Cursor).Order("id DESC")
		} else {
			query = query.Where("id > ?", *params.Cursor).Order("id ASC")
		}
	} else {
		query = query.Order("id DESC")
	}

	result := query.Find(&messageModels)
	if result.Error != nil {
		log.Error(ctx, "Error getting unread messages: ", result.Error)
		return nil, nil, result.Error
	}

	hasMore := false
	if len(messageModels) > limit {
		hasMore = true
		messageModels = messageModels[:limit]
	}

	paginationResult := &response.PaginationResult{
		HasMore: hasMore,
	}

	if len(messageModels) > 0 {
		if direction == constant.NewerDirection {
			// Reverse array for newer direction
			for i, j := 0, len(messageModels)-1; i < j; i, j = i+1, j-1 {
				messageModels[i], messageModels[j] = messageModels[j], messageModels[i]
			}
		}

		lastID := messageModels[len(messageModels)-1].ID
		firstID := messageModels[0].ID
		paginationResult.NextCursor = &lastID
		paginationResult.PreviousCursor = &firstID
	}

	return mapper.ToMessageEntities(messageModels), paginationResult, nil
}

// GetMessagesByStatus retrieves messages by specific status with pagination support
func (m MessageAdapter) GetMessagesByStatus(ctx context.Context, roomID int64, status constant.MessageStatus, params *request.MessageQueryParams) ([]*entity.MessageEntity, *response.PaginationResult, error) {
	var messageModels []*model.MessageModel

	limit := constant.DefaultLimitMessage
	if params.Limit > 0 {
		limit = params.Limit
	}
	if limit > constant.MaxLimitMessage {
		limit = constant.MaxLimitMessage
	}

	query := m.db.WithContext(ctx).
		Where("chat_room_id = ? AND status = ?", roomID, string(status)).
		Limit(limit + 1)

	if params.SenderID != nil {
		query = query.Where("sender_id = ?", *params.SenderID)
	}

	if params.MessageType != nil {
		query = query.Where("type = ?", *params.MessageType)
	}

	// Cursor-based pagination
	direction := constant.OlderDirection
	if params.Direction == constant.NewerDirection {
		direction = constant.NewerDirection
	}

	if params.Cursor != nil {
		if direction == constant.OlderDirection {
			query = query.Where("id < ?", *params.Cursor).Order("id DESC")
		} else {
			query = query.Where("id > ?", *params.Cursor).Order("id ASC")
		}
	} else {
		query = query.Order("id DESC")
	}

	result := query.Find(&messageModels)
	if result.Error != nil {
		log.Error(ctx, "Error getting messages by status: ", result.Error)
		return nil, nil, result.Error
	}

	hasMore := false
	if len(messageModels) > limit {
		hasMore = true
		messageModels = messageModels[:limit]
	}

	paginationResult := &response.PaginationResult{
		HasMore: hasMore,
	}

	if len(messageModels) > 0 {
		if direction == constant.NewerDirection {
			// Reverse array for newer direction
			for i, j := 0, len(messageModels)-1; i < j; i, j = i+1, j-1 {
				messageModels[i], messageModels[j] = messageModels[j], messageModels[i]
			}
		}

		lastID := messageModels[len(messageModels)-1].ID
		firstID := messageModels[0].ID
		paginationResult.NextCursor = &lastID
		paginationResult.PreviousCursor = &firstID

		// Get total count for this status
		var count int64
		m.db.WithContext(ctx).Model(&model.MessageModel{}).
			Where("chat_room_id = ? AND status = ?", roomID, string(status)).
			Count(&count)
		paginationResult.TotalItems = count
	}

	return mapper.ToMessageEntities(messageModels), paginationResult, nil
}

func NewMessageAdapter(db *gorm.DB) port.IMessagePort {
	return &MessageAdapter{
		base: base{
			db: db,
		},
	}
}
