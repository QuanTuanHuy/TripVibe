package usecase

import (
	"chat_service/core/domain/constant"
	"chat_service/core/domain/entity"
	"chat_service/core/port"
	"context"
	"errors"
	"github.com/golibs-starter/golib/log"
)

type IGetChatRoomUseCase interface {
	GetChatRoomById(ctx context.Context, roomID, userID int64) (*entity.ChatRoomEntity, error)
}

type GetChatRoomUseCase struct {
	chatRoomPort        port.IChatRoomPort
	roomParticipantPort port.IRoomParticipantPort
}

func (g GetChatRoomUseCase) GetChatRoomById(ctx context.Context, roomID, userID int64) (*entity.ChatRoomEntity, error) {
	chatRoom, err := g.chatRoomPort.GetChatRoomByID(ctx, roomID)
	if err != nil {
		log.Error(ctx, "Get chat room by id failed, ", err)
		return nil, err
	}

	_, err = g.roomParticipantPort.GetByRoomIDAndParticipantID(ctx, roomID, userID)
	if err != nil {
		if err.Error() == constant.ErrRoomParticipantNotFound {
			return nil, errors.New(constant.ErrForbiddenGetChatRoom)
		}
		return nil, err
	}

	return chatRoom, nil
}

func NewGetChatRoomUseCase(chatRoomPort port.IChatRoomPort, roomParticipantPort port.IRoomParticipantPort) IGetChatRoomUseCase {
	return &GetChatRoomUseCase{
		chatRoomPort:        chatRoomPort,
		roomParticipantPort: roomParticipantPort,
	}
}
