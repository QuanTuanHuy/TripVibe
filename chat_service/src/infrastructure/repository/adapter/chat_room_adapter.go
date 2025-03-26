package adapter

import (
	"chat_service/core/domain/constant"
	"chat_service/core/domain/entity"
	"chat_service/core/port"
	"chat_service/infrastructure/repository/mapper"
	"chat_service/infrastructure/repository/model"
	"context"
	"errors"
	"gorm.io/gorm"
)

type ChatRoomAdapter struct {
	base
}

func (c ChatRoomAdapter) CreateChatRoom(ctx context.Context, tx *gorm.DB, chatRoom *entity.ChatRoomEntity) (*entity.ChatRoomEntity, error) {
	chatRoomModel := mapper.ToChatRoomModel(chatRoom)
	if err := tx.WithContext(ctx).Create(chatRoomModel).Error; err != nil {
		return nil, err
	}
	return mapper.ToChatRoomEntity(chatRoomModel), nil
}

func (c ChatRoomAdapter) UpdateChatRoom(ctx context.Context, tx *gorm.DB, chatRoom *entity.ChatRoomEntity) (*entity.ChatRoomEntity, error) {
	chatRoomModel := mapper.ToChatRoomModel(chatRoom)
	if err := tx.WithContext(ctx).Where("id = ?", chatRoom.ID).Updates(chatRoomModel).Error; err != nil {
		return nil, err
	}
	return mapper.ToChatRoomEntity(chatRoomModel), nil
}

func (c ChatRoomAdapter) GetChatRoomByBookingID(ctx context.Context, bookingID int64) (*entity.ChatRoomEntity, error) {
	var chatRoom *model.ChatRoomModel
	if err := c.db.WithContext(ctx).
		Model(&model.ChatRoomModel{}).
		Where("booking_id = ?", bookingID).
		First(&chatRoom).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, errors.New(constant.ErrChatRoomNotFound)
		}
		return nil, err
	}
	return mapper.ToChatRoomEntity(chatRoom), nil
}

func NewChatRoomAdapter(db *gorm.DB) port.IChatRoomPort {
	return &ChatRoomAdapter{
		base: base{db: db},
	}
}
