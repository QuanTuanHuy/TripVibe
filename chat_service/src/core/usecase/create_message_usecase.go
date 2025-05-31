package usecase

import (
	"chat_service/core/domain/constant"
	"chat_service/core/domain/entity"
	"chat_service/core/port"
	"chat_service/kernel/utils"
	"context"
	"errors"
	"mime/multipart"

	"github.com/golibs-starter/golib/log"
)

type ICreateMessageUseCase interface {
	CreateMessage(ctx context.Context, roomID, senderID int64, content string, messageType constant.MessageType) (*entity.MessageEntity, error)
	CreateMediaMessage(ctx context.Context, roomID, senderID int64, file *multipart.FileHeader) (*entity.MessageEntity, error)
}

type CreateMessageUseCase struct {
	messagePort          port.IMessagePort
	mediaDataPort        port.IMediaDataPort
	chatRoomPort         port.IChatRoomPort
	fileServicePort      port.IFileServicePort
	dbTransactionUseCase IDatabaseTransactionUseCase
	getChatRoomUseCase   IGetChatRoomUseCase
}

func (c *CreateMessageUseCase) CreateMediaMessage(ctx context.Context, roomID int64, senderID int64, file *multipart.FileHeader) (*entity.MessageEntity, error) {
	chatRoom, err := c.getChatRoomUseCase.GetChatRoomById(ctx, roomID, senderID)
	if err != nil {
		log.Error(ctx, "Get chat room by id failed, ", err)
		if err.Error() == constant.ErrForbiddenGetChatRoom {
			return nil, errors.New(constant.ErrForbiddenSendMessage)
		}
		return nil, err
	}

	mimeType := file.Header.Get("Content-Type")
	mediaType := utils.DetermineMediaType(mimeType)

	uploadResponse, err := c.fileServicePort.UploadFile(ctx, file)
	if err != nil {
		log.Error(ctx, "Upload file failed, ", err)
		return nil, err
	}

	tx := c.dbTransactionUseCase.StartTransaction()
	defer func() {
		if errRollBack := c.dbTransactionUseCase.Rollback(tx); errRollBack != nil {
			log.Error(ctx, "Rollback create message failed, : ", errRollBack)
		} else {
			log.Info(ctx, "Rollback create message success")
		}
	}()

	mediaData := &entity.MediaDataEntity{
		BaseEntity: entity.BaseEntity{
			ID: uploadResponse.ID,
		},
		URL:          uploadResponse.URL,
		FileName:     file.Filename,
		FileSize:     file.Size,
		MimeType:     mimeType,
		MediaType:    mediaType,
		ThumbnailURL: nil,
	}
	mediaData, err = c.mediaDataPort.CreateMedia(ctx, tx, mediaData)
	if err != nil {
		log.Error(ctx, "Create media data failed, ", err)
		return nil, err
	}

	message := &entity.MessageEntity{
		ChatRoomID:  roomID,
		SenderID:    &senderID,
		Type:        constant.MediaMessage,
		IsRead:      false,
		MediaDataID: &mediaData.ID,
	}
	message, err = c.messagePort.CreateMessage(ctx, tx, message)
	if err != nil {
		log.Error(ctx, "Create message failed, ", err)
		return nil, err
	}
	message.MediaData = mediaData

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

func NewCreateMessageUseCase(
	messagePort port.IMessagePort,
	mediaDataPort port.IMediaDataPort,
	dbTransactionUseCase IDatabaseTransactionUseCase,
	getChatRoomUseCase IGetChatRoomUseCase,
	fileServicePort port.IFileServicePort,
	chatRoomPort port.IChatRoomPort) ICreateMessageUseCase {
	return &CreateMessageUseCase{
		messagePort:          messagePort,
		mediaDataPort:        mediaDataPort,
		chatRoomPort:         chatRoomPort,
		fileServicePort:      fileServicePort,
		dbTransactionUseCase: dbTransactionUseCase,
		getChatRoomUseCase:   getChatRoomUseCase,
	}
}
