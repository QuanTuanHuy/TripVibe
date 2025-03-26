package mapper

import (
	"chat_service/core/domain/entity"
	"chat_service/infrastructure/repository/model"
)

func ToChatRoomEntity(chatRoom *model.ChatRoomModel) *entity.ChatRoomEntity {
	return &entity.ChatRoomEntity{
		BaseEntity: entity.BaseEntity{
			ID:        chatRoom.ID,
			CreatedAt: chatRoom.CreatedAt.Unix(),
			UpdatedAt: chatRoom.UpdatedAt.Unix(),
		},
		BookingID:     chatRoom.BookingID,
		LastMessageID: chatRoom.LastMessageID,
	}
}

func ToChatRoomModel(chatRoom *entity.ChatRoomEntity) *model.ChatRoomModel {
	return &model.ChatRoomModel{
		BaseModel: model.BaseModel{
			ID: chatRoom.ID,
		},
		BookingID:     chatRoom.BookingID,
		LastMessageID: chatRoom.LastMessageID,
	}
}
