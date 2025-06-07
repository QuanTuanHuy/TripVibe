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
	CountUnreadMessages(ctx context.Context, roomID, userID int64) (int64, error)
}

type GetMessageUseCase struct {
	getChatRoomUseCase IGetChatRoomUseCase
	messagePort        port.IMessagePort
	mediaDataPort      port.IMediaDataPort
}

func (g GetMessageUseCase) CountUnreadMessages(ctx context.Context, roomID, userID int64) (int64, error) {
	// check permission
	_, err := g.getChatRoomUseCase.GetChatRoomById(ctx, roomID, userID)
	if err != nil {
		log.Error(ctx, "Get chat room by id failed, ", err)
		return 0, err
	}

	return g.messagePort.CountUnreadMessages(ctx, roomID, userID)
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

	messages, pagination, err := g.messagePort.GetMessagesByRoomId(ctx, chatRoomID, params)
	if err != nil {
		log.Error(ctx, "Get messages by room id failed, ", err)
		return nil, nil, err
	}
	if len(messages) > 0 {
		mediaIDs := make([]int64, 0)
		for _, msg := range messages {
			if msg.MediaDataID != nil {
				mediaIDs = append(mediaIDs, *msg.MediaDataID)
			}
		}

		if len(mediaIDs) > 0 {
			mediaData, err := g.mediaDataPort.GetMediaDataByIDs(ctx, mediaIDs)
			if err != nil {
				log.Error(ctx, "Get media data by ids failed, ", err)
				return messages, pagination, err
			}

			mediaDataMap := make(map[int64]*entity.MediaDataEntity)
			for _, media := range mediaData {
				mediaDataMap[media.ID] = media
			}

			for _, msg := range messages {
				if msg.MediaDataID != nil {
					msg.MediaData = mediaDataMap[*msg.MediaDataID]
				}
			}
		}
	}
	return messages, pagination, nil
}

func NewGetMessageUseCase(
	getChatRoomUseCase IGetChatRoomUseCase,
	messagePort port.IMessagePort,
	mediaDataPort port.IMediaDataPort) IGetMessageUseCase {
	return &GetMessageUseCase{
		getChatRoomUseCase: getChatRoomUseCase,
		messagePort:        messagePort,
		mediaDataPort:      mediaDataPort,
	}
}
