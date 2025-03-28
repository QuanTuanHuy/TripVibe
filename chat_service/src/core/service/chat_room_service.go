package service

import (
	"chat_service/core/domain/constant"
	"chat_service/core/domain/dto"
	"chat_service/core/domain/dto/request"
	"chat_service/core/domain/dto/response"
	"chat_service/core/domain/entity"
	"chat_service/core/usecase"
	"context"
)

type IChatRoomService interface {
	CreateChatRoom(ctx context.Context, bookingID int64, tourist *dto.ParticipantDto, owner *dto.ParticipantDto) (*entity.ChatRoomEntity, error)
	CreateMessage(ctx context.Context, roomID, senderID int64, content string, messageType constant.MessageType) (*entity.MessageEntity, error)
	GetMessagesByRoomId(ctx context.Context, userID, chatRoomID int64, params *request.MessageQueryParams) ([]*entity.MessageEntity, *response.PaginationResult, error)
	GetChatRooms(ctx context.Context, params *request.ChatRoomQueryParams) ([]*entity.ChatRoomEntity, error)
}

type ChatRoomService struct {
	createChatRoomUseCase usecase.ICreateChatRoomUseCase
	createMessageUseCase  usecase.ICreateMessageUseCase
	getMessageUseCase     usecase.IGetMessageUseCase
	getChatRoomUseCase    usecase.IGetChatRoomUseCase
}

func (c ChatRoomService) GetChatRooms(ctx context.Context, params *request.ChatRoomQueryParams) ([]*entity.ChatRoomEntity, error) {
	return c.getChatRoomUseCase.GetChatRooms(ctx, params)
}

func (c ChatRoomService) GetMessagesByRoomId(ctx context.Context, userID, chatRoomID int64, params *request.MessageQueryParams) ([]*entity.MessageEntity, *response.PaginationResult, error) {
	return c.getMessageUseCase.GetMessagesByRoomId(ctx, userID, chatRoomID, params)
}

func (c ChatRoomService) CreateMessage(ctx context.Context, roomID, senderID int64, content string, messageType constant.MessageType) (*entity.MessageEntity, error) {
	return c.createMessageUseCase.CreateMessage(ctx, roomID, senderID, content, messageType)
}

func (c ChatRoomService) CreateChatRoom(ctx context.Context, bookingID int64, tourist *dto.ParticipantDto, owner *dto.ParticipantDto) (*entity.ChatRoomEntity, error) {
	return c.createChatRoomUseCase.CreateChatRoom(ctx, bookingID, tourist, owner)
}

func NewChatRoomService(createChatRoomUseCase usecase.ICreateChatRoomUseCase,
	createMessageUseCase usecase.ICreateMessageUseCase,
	getMessageUseCase usecase.IGetMessageUseCase,
	getChatRoomUseCase usecase.IGetChatRoomUseCase) IChatRoomService {
	return &ChatRoomService{
		createChatRoomUseCase: createChatRoomUseCase,
		createMessageUseCase:  createMessageUseCase,
		getMessageUseCase:     getMessageUseCase,
		getChatRoomUseCase:    getChatRoomUseCase,
	}
}
