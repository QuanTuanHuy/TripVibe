package usecase

import (
	"chat_service/core/port"
	"context"
	"github.com/golibs-starter/golib/log"
)

type IUpdateMessageUseCase interface {
	MarkMessageAsRead(ctx context.Context, chatRoomID, userID, messageID int64) error
}

type UpdateMessageUseCase struct {
	messagePort          port.IMessagePort
	getChatRoomUseCase   IGetChatRoomUseCase
	dbTransactionUseCase IDatabaseTransactionUseCase
}

func (u UpdateMessageUseCase) MarkMessageAsRead(ctx context.Context, roomID, userID, messageID int64) error {
	// check permission
	_, err := u.getChatRoomUseCase.GetChatRoomById(ctx, roomID, userID)
	if err != nil {
		log.Error(ctx, "Get chat room by id failed, ", err)
		return err
	}

	tx := u.dbTransactionUseCase.StartTransaction()
	defer func() {
		if errRollBack := u.dbTransactionUseCase.Rollback(tx); errRollBack != nil {
			log.Error(ctx, "Rollback update message failed, : ", errRollBack)
		} else {
			log.Info(ctx, "Rollback update message success")
		}
	}()

	err = u.messagePort.MarkMessageAsRead(ctx, tx, roomID, userID, messageID)
	if err != nil {
		log.Error(ctx, "Mark message as read failed, ", err)
		return err
	}

	errCommit := u.dbTransactionUseCase.Commit(tx)
	if errCommit != nil {
		log.Error(ctx, "Commit update message failed, : ", errCommit)
		return errCommit
	}

	return nil
}

func NewUpdateMessageUseCase(messagePort port.IMessagePort,
	getChatRoomUseCase IGetChatRoomUseCase,
	dbTransactionUseCase IDatabaseTransactionUseCase) IUpdateMessageUseCase {
	return &UpdateMessageUseCase{
		messagePort:          messagePort,
		getChatRoomUseCase:   getChatRoomUseCase,
		dbTransactionUseCase: dbTransactionUseCase,
	}
}
