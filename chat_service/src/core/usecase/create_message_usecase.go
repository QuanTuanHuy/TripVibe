package usecase

import (
	"chat_service/core/domain/constant"
	"chat_service/core/domain/entity"
	"chat_service/core/port"
	"context"
	"errors"
	"github.com/golibs-starter/golib/log"
)

type ICreateMessageUseCase interface {
	CreateMessage(ctx context.Context, roomID, senderID int64, content string, messageType constant.MessageType) (*entity.MessageEntity, error)
}

type CreateMessageUseCase struct {
	messagePort          port.IMessagePort
	chatRoomPort         port.IChatRoomPort
	dbTransactionUseCase IDatabaseTransactionUseCase
	getChatRoomUseCase   IGetChatRoomUseCase
}

func (c CreateMessageUseCase) CreateMessage(ctx context.Context, roomID, senderID int64, content string, messageType constant.MessageType) (*entity.MessageEntity, error) {
	chatRoom, err := c.getChatRoomUseCase.GetChatRoomById(ctx, roomID, senderID)
	if err != nil {
		log.Error(ctx, "Get chat room by id failed, ", err)
		if err.Error() == constant.ErrForbiddenGetChatRoom {
			return nil, errors.New(constant.ErrForbiddenSendMessage)
		}
		return nil, err
	}

	message := &entity.MessageEntity{
		ChatRoomID: roomID,
		SenderID:   &senderID,
		Content:    content,
		Type:       messageType,
		IsRead:     false,
	}
	tx := c.dbTransactionUseCase.StartTransaction()
	defer func() {
		if errRollBack := c.dbTransactionUseCase.Rollback(tx); errRollBack != nil {
			log.Error(ctx, "Rollback create message failed, : ", errRollBack)
		} else {
			log.Info(ctx, "Rollback create message success")
		}
	}()
	message, err = c.messagePort.CreateMessage(ctx, tx, message)
	if err != nil {
		log.Error(ctx, "Create message failed, ", err)
		return nil, err
	}

	// update chatroom
	chatRoom.LastMessageID = message.ID
	_, err = c.chatRoomPort.UpdateChatRoom(ctx, tx, chatRoom)
	if err != nil {
		log.Error(ctx, "Update chat room failed, ", err)
		return nil, err
	}

	if err := c.dbTransactionUseCase.Commit(tx); err != nil {
		log.Error(ctx, "Commit create message failed, ", err)
		return nil, err
	}

	return message, nil
}

func NewCreateMessageUseCase(messagePort port.IMessagePort,
	dbTransactionUseCase IDatabaseTransactionUseCase,
	getChatRoomUseCase IGetChatRoomUseCase,
	chatRoomPort port.IChatRoomPort) ICreateMessageUseCase {
	return &CreateMessageUseCase{
		messagePort:          messagePort,
		chatRoomPort:         chatRoomPort,
		dbTransactionUseCase: dbTransactionUseCase,
		getChatRoomUseCase:   getChatRoomUseCase,
	}
}
