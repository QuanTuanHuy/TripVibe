package controller

import (
	"chat_service/core/domain/common"
	"chat_service/core/domain/constant"
	"chat_service/core/domain/dto"
	request2 "chat_service/core/domain/dto/request"
	"chat_service/core/service"
	"chat_service/kernel/apihelper"
	"chat_service/kernel/utils"
	"chat_service/ui/resource/request"
	"github.com/gin-gonic/gin"
	"github.com/golibs-starter/golib/log"
	"strconv"
)

type ChatController struct {
	chatRoomService service.IChatRoomService
}

func NewChatController(chatRoomService service.IChatRoomService) *ChatController {
	return &ChatController{chatRoomService: chatRoomService}
}

func (ch *ChatController) GetChatRooms(c *gin.Context) {
	var userID, chatUserID int64
	var err error
	userIDFromContext, exists := c.Get("userID")
	if exists {
		userID = userIDFromContext.(int64)
	} else {
		// Fallback: lấy từ query parameter
		userIDStr := c.Query("userId")
		if userIDStr == "" {
			log.Error(c, "User ID is required")
			apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
			return
		}

		userID, err = strconv.ParseInt(userIDStr, 10, 64)
		if err != nil {
			log.Error(c, "Parse user id failed, ", err)
			apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
			return
		}
	}

	chatUserIDStr := c.Query("chatUserId")
	if chatUserIDStr != "" {
		chatUserID, err = strconv.ParseInt(chatUserIDStr, 10, 64)
		if err != nil {
			log.Error(c, "Parse chat user id failed, ", err)
			apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
			return
		}
	}

	pageSize, page := utils.GetPagingParams(c)

	var params request2.ChatRoomQueryParams
	params.UserID = &userID
	params.ChatUserID = &chatUserID
	params.Page = &page
	params.PageSize = &pageSize

	chatRooms, err := ch.chatRoomService.GetChatRooms(c, &params)
	if err != nil {
		log.Error(c, "Get chat rooms failed, ", err)
		apihelper.AbortErrorHandle(c, common.GeneralServiceUnavailable)
		return
	}

	apihelper.SuccessfulHandle(c, chatRooms)
}

func (ch *ChatController) GetMessagesByRoomID(c *gin.Context) {
	roomID, err := strconv.ParseInt(c.Param("roomId"), 10, 64)
	if err != nil {
		log.Error(c, "Parse room id failed, ", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	var userID int64
	userIDFromContext, exists := c.Get("userID")
	if exists {
		userID = userIDFromContext.(int64)
	} else {
		// Fallback: lấy từ query parameter
		userIDStr := c.Query("userId")
		if userIDStr == "" {
			log.Error(c, "User ID is required")
			apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
			return
		}

		userID, err = strconv.ParseInt(userIDStr, 10, 64)
		if err != nil {
			log.Error(c, "Parse user id failed, ", err)
			apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
			return
		}
	}

	var params request2.MessageQueryParams

	params.Direction = constant.OlderDirection
	params.Limit = constant.DefaultLimitMessage

	limitStr := c.Query("limit")
	if limitStr != "" {
		limit, err := strconv.ParseInt(limitStr, 10, 32)
		if err != nil {
			log.Error(c, "Parse limit failed, ", err)
			apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
			return
		}
		params.Limit = int(limit)
	}

	direction := c.Query("direction")
	if direction != "" {
		params.Direction = direction
	}

	cursorStr := c.Query("cursor")
	if cursorStr != "" {
		cursor, err := strconv.ParseInt(cursorStr, 10, 64)
		if err != nil {
			log.Error(c, "Parse cursor failed, ", err)
			apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
			return
		}
		params.Cursor = &cursor
	}

	messageType := c.Query("messageType")
	if messageType != "" {
		params.MessageType = &messageType
	}

	senderIDStr := c.Query("senderId")
	if senderIDStr != "" {
		senderID, err := strconv.ParseInt(senderIDStr, 10, 64)
		if err != nil {
			log.Error(c, "Parse sender id failed, ", err)
			apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
			return
		}
		params.SenderID = &senderID
	}

	messages, pagination, err := ch.chatRoomService.GetMessagesByRoomId(c, userID, roomID, &params)
	if err != nil {
		log.Error(c, "Get messages failed, ", err)

		if err.Error() == constant.ErrForbiddenGetMessage {
			apihelper.AbortErrorHandle(c, common.GeneralForbidden)
			return
		}
		if err.Error() == constant.ErrChatRoomNotFound {
			apihelper.AbortErrorHandle(c, common.GeneralNotFound)
			return
		}

		apihelper.AbortErrorHandle(c, common.GeneralServiceUnavailable)
		return
	}

	apihelper.SuccessfulHandle(c, gin.H{
		"messages":   messages,
		"pagination": pagination,
		"roomId":     roomID,
		"meta": gin.H{
			"direction": params.Direction,
			"hasMore":   pagination.HasMore,
		},
	})
}

func (ch *ChatController) SendMessage(c *gin.Context) {
	roomID, err := strconv.ParseInt(c.Param("roomId"), 10, 64)
	if err != nil {
		log.Error(c, "Parse room id failed, ", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	var req request.CreateMessageRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		log.Error(c, "Bind request failed, ", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	messageType := constant.MessageType(req.MessageType)
	message, err := ch.chatRoomService.CreateMessage(c, roomID, req.SenderID, req.Content, messageType)
	if err != nil {
		if err.Error() == constant.ErrForbiddenSendMessage {
			apihelper.AbortErrorHandle(c, common.GeneralForbidden)
			return
		}
		apihelper.AbortErrorHandle(c, common.GeneralServiceUnavailable)
		return
	}

	apihelper.SuccessfulHandle(c, message)
}

func (ch *ChatController) CreateChatRoom(c *gin.Context) {
	var req struct {
		BookingID int64 `json:"bookingId"`
		Tourist   *dto.ParticipantDto
		Owner     *dto.ParticipantDto
	}

	if err := c.ShouldBindJSON(&req); err != nil {
		log.Error(c, "Bind request failed, ", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	chatRoom, err := ch.chatRoomService.CreateChatRoom(c, req.BookingID, req.Tourist, req.Owner)
	if err != nil {
		log.Error(c, "Create chat room failed, ", err)
		apihelper.AbortErrorHandle(c, common.GeneralServiceUnavailable)
		return
	}

	apihelper.SuccessfulHandle(c, chatRoom)
}
