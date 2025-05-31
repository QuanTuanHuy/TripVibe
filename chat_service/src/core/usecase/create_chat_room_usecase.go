package usecase

import (
	"chat_service/core/domain/constant"
	response "chat_service/core/domain/dto"
	"chat_service/core/domain/dto/request"
	"chat_service/core/domain/entity"
	"chat_service/core/port"
	"context"

	"github.com/golibs-starter/golib/log"
)

type ICreateChatRoomUseCase interface {
	CreateChatRoom(ctx context.Context, bookingID int64, tourist *response.ParticipantDto, owner *response.ParticipantDto) (*entity.ChatRoomEntity, error)
}

type CreateChatRoomUseCase struct {
	chatRoomPort         port.IChatRoomPort
	participantPort      port.IParticipantPort
	roomParticipantPort  port.IRoomParticipantPort
	messagePort          port.IMessagePort
	dbTransactionUseCase IDatabaseTransactionUseCase
}

func (c CreateChatRoomUseCase) CreateChatRoom(ctx context.Context, bookingID int64, tourist *response.ParticipantDto, owner *response.ParticipantDto) (*entity.ChatRoomEntity, error) {
	_, err := c.chatRoomPort.GetChatRoomByBookingID(ctx, bookingID)
	if err != nil && err.Error() != constant.ErrChatRoomNotFound {
		log.Error(ctx, "Get chat room by booking id failed, ", err)
		return nil, err
	}

	// check existed chat room
	params := &request.ChatRoomQueryParams{
		UserID:     &tourist.UserID,
		ChatUserID: &owner.UserID,
	}
	chatRooms, err := c.chatRoomPort.GetChatRooms(ctx, params)
	if err != nil {
		log.Error(ctx, "Get chat rooms failed, ", err)
		return nil, err
	}
	if len(chatRooms) > 0 {
		log.Info(ctx, "Chat room already existed, ID: ", chatRooms[0].ID)
		return chatRooms[0], nil
	}

	chatRoom := &entity.ChatRoomEntity{
		BookingID: bookingID,
	}
	tx := c.dbTransactionUseCase.StartTransaction()
	defer func() {
		if errRollBack := c.dbTransactionUseCase.Rollback(tx); errRollBack != nil {
			log.Error(ctx, "Rollback create chatroom failed, : ", errRollBack)
		} else {
			log.Info(ctx, "Rollback create chatroom success")
		}
	}()
	// create chat room
	chatRoom, err = c.chatRoomPort.CreateChatRoom(ctx, tx, chatRoom)
	if err != nil {
		log.Error(ctx, "Create chat room failed, ", err)
		return nil, err
	}
	// create participants
	existedTourist, err := c.participantPort.GetParticipantByID(ctx, tourist.UserID)
	if err != nil && err.Error() != constant.ErrParticipantNotFound {
		log.Error(ctx, "Get tourist failed, ", err)
		return nil, err
	}
	if existedTourist == nil {
		touristEntity := &entity.ParticipantEntity{
			UserID:   tourist.UserID,
			UserName: tourist.UserName,
			Role:     constant.Tourist,
		}
		existedTourist, err = c.participantPort.CreateParticipant(ctx, tx, touristEntity)
		if err != nil {
			log.Error(ctx, "Create tourist failed, ", err)
			return nil, err
		}
	}

	existedOwner, err := c.participantPort.GetParticipantByID(ctx, owner.UserID)
	if err != nil && err.Error() != constant.ErrParticipantNotFound {
		log.Error(ctx, "Get owner failed, ", err)
		return nil, err
	}
	if existedOwner == nil {
		ownerEntity := &entity.ParticipantEntity{
			UserID:   owner.UserID,
			UserName: owner.UserName,
			Role:     constant.Owner,
		}
		existedOwner, err = c.participantPort.CreateParticipant(ctx, tx, ownerEntity)
		if err != nil {
			log.Error(ctx, "Create owner failed, ", err)
			return nil, err
		}
	}
	// create room participant
	var roomParticipants []*entity.RoomParticipantEntity
	roomParticipants = append(roomParticipants, &entity.RoomParticipantEntity{
		ChatRoomID:    chatRoom.ID,
		ParticipantID: owner.UserID,
	})
	roomParticipants = append(roomParticipants, &entity.RoomParticipantEntity{
		ChatRoomID:    chatRoom.ID,
		ParticipantID: tourist.UserID,
	})
	_, err = c.roomParticipantPort.CreateRoomParticipants(ctx, tx, roomParticipants)

	// create message
	systemMsg := &entity.MessageEntity{
		ChatRoomID: chatRoom.ID,
		Content:    "Phòng chat đã được tạo. Bạn có thể bắt đầu trò chuyện.",
		Type:       constant.SystemMessage,
	}
	systemMsg, err = c.messagePort.CreateMessage(ctx, tx, systemMsg)
	if err != nil {
		log.Error(ctx, "Create system message failed, ", err)
		return nil, err
	}
	chatRoom.LastMessageID = systemMsg.ID
	chatRoom, err = c.chatRoomPort.UpdateChatRoom(ctx, tx, chatRoom)

	// commit transaction
	if errCommit := c.dbTransactionUseCase.Commit(tx); errCommit != nil {
		log.Error(ctx, "commit transaction failed, ", errCommit)
		return nil, errCommit
	}

	return chatRoom, nil
}

func NewCreateChatRoomUseCase(
	chatRoomPort port.IChatRoomPort,
	participantPort port.IParticipantPort,
	roomParticipantPort port.IRoomParticipantPort,
	messagePort port.IMessagePort,
	dbTransactionUseCase IDatabaseTransactionUseCase) ICreateChatRoomUseCase {
	return &CreateChatRoomUseCase{
		chatRoomPort:         chatRoomPort,
		participantPort:      participantPort,
		roomParticipantPort:  roomParticipantPort,
		messagePort:          messagePort,
		dbTransactionUseCase: dbTransactionUseCase,
	}
}
