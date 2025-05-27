package service

import (
	"chat_service/core/domain/constant"
	"chat_service/core/domain/dto"
	"chat_service/core/domain/dto/request"
	"chat_service/core/domain/dto/response"
	"chat_service/core/domain/entity"
	"chat_service/core/usecase"
	"chat_service/kernel/utils"
	"context"
	"github.com/golibs-starter/golib/log"
)

type IChatRoomService interface {
	CreateChatRoom(ctx context.Context, bookingID int64, tourist *dto.ParticipantDto, owner *dto.ParticipantDto) (*entity.ChatRoomEntity, error)
	CreateMessage(ctx context.Context, roomID, senderID int64, content string, messageType constant.MessageType) (*entity.MessageEntity, error)
	GetMessagesByRoomId(ctx context.Context, userID, chatRoomID int64, params *request.MessageQueryParams) ([]*entity.MessageEntity, *response.PaginationResult, error)
	GetChatRooms(ctx context.Context, params *request.ChatRoomQueryParams) (*response.GetChatRoomResponse, error)
	MarkMessageAsRead(ctx context.Context, roomID, userID, messageID int64) error
	CountUnreadMessages(ctx context.Context, roomID, userID int64) (int64, error)
	GetChatRoomByID(ctx context.Context, userID, chatRoomID int64) (*entity.ChatRoomEntity, error)
	// New methods for message status updates
	UpdateMessageStatus(ctx context.Context, messageID int64, status constant.MessageStatus, userID int64) (*entity.MessageEntity, error)
	MarkMessagesAsDelivered(ctx context.Context, roomID, userID int64, messageIDs []int64) error
}

type ChatRoomService struct {
	createChatRoomUseCase usecase.ICreateChatRoomUseCase
	createMessageUseCase  usecase.ICreateMessageUseCase
	getMessageUseCase     usecase.IGetMessageUseCase
	getChatRoomUseCase    usecase.IGetChatRoomUseCase
	updateMessageUseCase  usecase.IUpdateMessageUseCase
}

func (c ChatRoomService) GetChatRoomByID(ctx context.Context, userID, chatRoomID int64) (*entity.ChatRoomEntity, error) {
	return c.getChatRoomUseCase.GetChatRoomById(ctx, chatRoomID, userID)
}

func (c ChatRoomService) CountUnreadMessages(ctx context.Context, roomID, userID int64) (int64, error) {
	return c.getMessageUseCase.CountUnreadMessages(ctx, roomID, userID)
}

func (c ChatRoomService) MarkMessageAsRead(ctx context.Context, roomID, userID, messageID int64) error {
	return c.updateMessageUseCase.MarkMessageAsRead(ctx, roomID, userID, messageID)
}

func (c ChatRoomService) GetChatRooms(ctx context.Context, params *request.ChatRoomQueryParams) (*response.GetChatRoomResponse, error) {
	chatRooms, err := c.getChatRoomUseCase.GetChatRooms(ctx, params)
	if err != nil {
		log.Error(ctx, "get chat rooms failed, ", err)
		return nil, err
	}

	total, err := c.getChatRoomUseCase.CountChatRooms(ctx, params)
	if err != nil {
		log.Error(ctx, "count chat rooms failed, ", err)
		return nil, err
	}

	page := int64(*params.Page)
	pageSize := int64(*params.PageSize)
	nextPage, prevPage, totalPage := utils.CalculateParameterForGetRequest(page, pageSize, total)

	return response.ToGetChatRoomResponse(chatRooms, page, pageSize, totalPage, total, prevPage, nextPage), nil
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

func (c ChatRoomService) UpdateMessageStatus(ctx context.Context, messageID int64, status constant.MessageStatus, userID int64) (*entity.MessageEntity, error) {
	return c.updateMessageUseCase.UpdateMessageStatus(ctx, messageID, status, userID)
}

func (c ChatRoomService) MarkMessagesAsDelivered(ctx context.Context, roomID, userID int64, messageIDs []int64) error {
	return c.updateMessageUseCase.MarkMessagesAsDelivered(ctx, roomID, userID, messageIDs)
}

func NewChatRoomService(createChatRoomUseCase usecase.ICreateChatRoomUseCase,
	createMessageUseCase usecase.ICreateMessageUseCase,
	getMessageUseCase usecase.IGetMessageUseCase,
	getChatRoomUseCase usecase.IGetChatRoomUseCase,
	updateMessageUseCase usecase.IUpdateMessageUseCase) IChatRoomService {
	return &ChatRoomService{
		createChatRoomUseCase: createChatRoomUseCase,
		createMessageUseCase:  createMessageUseCase,
		getMessageUseCase:     getMessageUseCase,
		getChatRoomUseCase:    getChatRoomUseCase,
		updateMessageUseCase:  updateMessageUseCase,
	}
}
