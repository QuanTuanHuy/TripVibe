package mapper

import (
	"chat_service/core/domain/entity"
	"chat_service/infrastructure/repository/model"
)

func ToChatRoomEntity(chatRoom *model.ChatRoomModel) *entity.ChatRoomEntity {
	if chatRoom == nil {
		return nil
	}
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
	if chatRoom == nil {
		return nil
	}
	return &model.ChatRoomModel{
		BaseModel: model.BaseModel{
			ID: chatRoom.ID,
		},
		BookingID:     chatRoom.BookingID,
		LastMessageID: chatRoom.LastMessageID,
	}
}

func ToChatRoomEntities(chatRooms []*model.ChatRoomModel) []*entity.ChatRoomEntity {
	var entities []*entity.ChatRoomEntity
	for _, cr := range chatRooms {
		entities = append(entities, ToChatRoomEntity(cr))
	}
	return entities
}
