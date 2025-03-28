package usecase

import (
	"chat_service/core/domain/constant"
	"chat_service/core/domain/dto/request"
	"chat_service/core/domain/dto/response"
	"chat_service/core/domain/entity"
	"chat_service/core/port"
	"context"
	"errors"
	"github.com/golibs-starter/golib/log"
)

type IGetMessageUseCase interface {
	GetMessagesByRoomId(ctx context.Context, userID, chatRoomID int64, params *request.MessageQueryParams) ([]*entity.MessageEntity, *response.PaginationResult, error)
	GetMessagesByIDs(ctx context.Context, IDs []int64) ([]*entity.MessageEntity, error)
}

type GetMessageUseCase struct {
	getChatRoomUseCase IGetChatRoomUseCase
	messagePort        port.IMessagePort
}

func (g GetMessageUseCase) GetMessagesByIDs(ctx context.Context, IDs []int64) ([]*entity.MessageEntity, error) {
	return g.messagePort.GetMessagesByIDs(ctx, IDs)
}

func (g GetMessageUseCase) GetMessagesByRoomId(ctx context.Context, userID, chatRoomID int64, params *request.MessageQueryParams) ([]*entity.MessageEntity, *response.PaginationResult, error) {
	_, err := g.getChatRoomUseCase.GetChatRoomById(ctx, chatRoomID, userID)
	if err != nil {
		log.Error(ctx, "Get chat room by id failed, ", err)
		if err.Error() == constant.ErrForbiddenGetChatRoom {
			return nil, nil, errors.New(constant.ErrForbiddenGetMessage)
		}
		return nil, nil, err
	}

	return g.messagePort.GetMessagesByRoomId(ctx, chatRoomID, params)
}

func NewGetMessageUseCase(getChatRoomUseCase IGetChatRoomUseCase, messagePort port.IMessagePort) IGetMessageUseCase {
	return &GetMessageUseCase{
		getChatRoomUseCase: getChatRoomUseCase,
		messagePort:        messagePort,
	}
}
