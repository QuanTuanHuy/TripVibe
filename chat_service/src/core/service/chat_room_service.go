package service

import (
	"chat_service/core/domain/dto"
	"chat_service/core/domain/entity"
	"chat_service/core/usecase"
	"context"
)

type IChatRoomService interface {
	CreateChatRoom(ctx context.Context, bookingID int64, tourist *dto.ParticipantDto, owner *dto.ParticipantDto) (*entity.ChatRoomEntity, error)
}

type ChatRoomService struct {
	createChatRoomUseCase usecase.ICreateChatRoomUseCase
}

func (c ChatRoomService) CreateChatRoom(ctx context.Context, bookingID int64, tourist *dto.ParticipantDto, owner *dto.ParticipantDto) (*entity.ChatRoomEntity, error) {
	return c.createChatRoomUseCase.CreateChatRoom(ctx, bookingID, tourist, owner)
}

func NewChatRoomService(createChatRoomUseCase usecase.ICreateChatRoomUseCase) IChatRoomService {
	return &ChatRoomService{
		createChatRoomUseCase: createChatRoomUseCase,
	}
}
