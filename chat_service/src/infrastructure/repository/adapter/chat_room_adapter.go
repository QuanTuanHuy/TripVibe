package adapter

import (
	"chat_service/core/domain/constant"
	"chat_service/core/domain/dto/request"
	"chat_service/core/domain/entity"
	"chat_service/core/port"
	"chat_service/infrastructure/repository/mapper"
	"chat_service/infrastructure/repository/model"
	"chat_service/infrastructure/repository/specification"
	"context"
	"errors"
	"gorm.io/gorm"
)

type ChatRoomAdapter struct {
	base
}

func (c ChatRoomAdapter) CountChatRooms(ctx context.Context, params *request.ChatRoomQueryParams) (int64, error) {
	var count int64
	query, args := specification.ToCountChatRoomSpecification(params)
	if err := c.db.WithContext(ctx).
		Model(&model.ChatRoomModel{}).
		Raw(query, args...).
		Count(&count).Error; err != nil {
		return 0, err
	}
	return count, nil
}

func (c ChatRoomAdapter) GetChatRooms(ctx context.Context, params *request.ChatRoomQueryParams) ([]*entity.ChatRoomEntity, error) {
	var chatRoomModels []*model.ChatRoomModel
	query, args := specification.ToGetChatRoomSpecification(params)
	if err := c.db.WithContext(ctx).
		Raw(query, args...).
		Find(&chatRoomModels).Error; err != nil {
		return nil, err
	}
	return mapper.ToChatRoomEntities(chatRoomModels), nil
}

func (c ChatRoomAdapter) GetChatRoomByID(ctx context.Context, chatRoomID int64) (*entity.ChatRoomEntity, error) {
	var chatRoom model.ChatRoomModel
	if err := c.db.WithContext(ctx).Where("id = ?", chatRoomID).First(&chatRoom).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, errors.New(constant.ErrChatRoomNotFound)
		}
		return nil, err
	}
	return mapper.ToChatRoomEntity(&chatRoom), nil
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
