package usecase

import (
	"chat_service/core/domain/constant"
	"chat_service/core/domain/dto/request"
	"chat_service/core/domain/entity"
	"chat_service/core/port"
	"context"
	"errors"
	"github.com/golibs-starter/golib/log"
)

type IGetChatRoomUseCase interface {
	GetChatRoomById(ctx context.Context, roomID, userID int64) (*entity.ChatRoomEntity, error)
	GetChatRooms(ctx context.Context, params *request.ChatRoomQueryParams) ([]*entity.ChatRoomEntity, error)
}

type GetChatRoomUseCase struct {
	chatRoomPort          port.IChatRoomPort
	roomParticipantPort   port.IRoomParticipantPort
	messagePort           port.IMessagePort
	getParticipantUseCase IGetParticipantUseCase
}

func (g GetChatRoomUseCase) GetChatRooms(ctx context.Context, params *request.ChatRoomQueryParams) ([]*entity.ChatRoomEntity, error) {
	chatRooms, err := g.chatRoomPort.GetChatRooms(ctx, params)
	if err != nil {
		log.Error(ctx, "Get chat rooms failed, ", err)
		return nil, err
	}
	if len(chatRooms) == 0 {
		return chatRooms, nil
	}

	// get participants
	roomIDs := make([]int64, 0)
	for _, chatRoom := range chatRooms {
		roomIDs = append(roomIDs, chatRoom.ID)
	}
	participants, err := g.getParticipantUseCase.GetParticipantsByRoomIDs(ctx, roomIDs)
	if err != nil {
		log.Error(ctx, "Get participants failed, ", err)
		return nil, err
	}

	// get last message
	messageIDs := make([]int64, 0)
	for _, chatRoom := range chatRooms {
		messageIDs = append(messageIDs, chatRoom.ID)
	}
	messages, err := g.messagePort.GetMessagesByIDs(ctx, messageIDs)
	if err != nil {
		log.Error(ctx, "Get messages failed, ", err)
		return nil, err
	}
	messageMap := make(map[int64]*entity.MessageEntity)
	for _, message := range messages {
		messageMap[message.ID] = message
	}

	for _, chatRoom := range chatRooms {
		chatRoom.Participants = participants[chatRoom.ID]
		chatRoom.LastMessage = messageMap[chatRoom.LastMessageID]
	}

	return chatRooms, nil
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

func NewGetChatRoomUseCase(chatRoomPort port.IChatRoomPort,
	roomParticipantPort port.IRoomParticipantPort,
	getParticipantUseCase IGetParticipantUseCase,
	messagePort port.IMessagePort) IGetChatRoomUseCase {
	return &GetChatRoomUseCase{
		chatRoomPort:          chatRoomPort,
		roomParticipantPort:   roomParticipantPort,
		getParticipantUseCase: getParticipantUseCase,
		messagePort:           messagePort,
	}
}
