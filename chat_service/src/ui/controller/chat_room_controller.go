package controller

import (
	"chat_service/core/domain/common"
	"chat_service/core/domain/constant"
	"chat_service/core/domain/dto"
	request2 "chat_service/core/domain/dto/request"
	"chat_service/core/domain/ws"
	"chat_service/core/service"
	"chat_service/infrastructure"
	"chat_service/kernel/apihelper"
	"chat_service/kernel/utils"
	"chat_service/ui/resource/request"
	"strconv"

	"github.com/gin-gonic/gin"
	"github.com/golibs-starter/golib/log"
)

type ChatController struct {
	chatRoomService service.IChatRoomService
	wsManager       *infrastructure.WebSocketManager // Add this field
}

func NewChatController(chatRoomService service.IChatRoomService, wsManager *infrastructure.WebSocketManager) *ChatController {
	return &ChatController{
		chatRoomService: chatRoomService,
		wsManager:       wsManager,
	}
}

func (ch *ChatController) GetChatRooms(c *gin.Context) {
	var chatUserID int64
	var err error

	userIDStr, ok := c.Get("userID")
	if !ok {
		log.Error(c, "error getting user id from context")
		apihelper.AbortErrorHandle(c, common.GeneralUnauthorized)
		return
	}
	userID, _ := userIDStr.(int64)

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

	userIDFromContext, exists := c.Get("userID")
	if !exists {
		log.Error(c, "User ID not found in context")
		apihelper.AbortErrorHandle(c, common.GeneralUnauthorized)
		return
	}
	userID := userIDFromContext.(int64)

	var queryParams request2.MessageQueryParams
	if err := c.ShouldBindQuery(&queryParams); err != nil {
		log.Error(c, "Bind query parameters failed, ", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	// Set defaults if not provided
	if queryParams.Limit <= 0 {
		queryParams.Limit = constant.DefaultLimitMessage
	}
	if queryParams.Direction == "" {
		queryParams.Direction = constant.OlderDirection
	}

	messages, pagination, err := ch.chatRoomService.GetMessagesByRoomId(c, userID, roomID, &queryParams)
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
			"direction": queryParams.Direction,
			"hasMore":   pagination.HasMore,
		},
	})
}

func (ch *ChatController) SendMediaMessage(c *gin.Context) {
	userID, exists := c.Get("userID")
	if !exists {
		log.Error(c, "User ID not found in context")
		apihelper.AbortErrorHandle(c, common.GeneralUnauthorized)
		return
	}
	roomID, err := strconv.ParseInt(c.Param("roomId"), 10, 64)
	if err != nil {
		log.Error(c, "Parse room id failed, ", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	file, err := c.FormFile("file")
	if err != nil {
		log.Error(c, "Get file from form failed, ", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}
	message, err := ch.chatRoomService.CreateMediaMessage(c, roomID, userID.(int64), file)
	if err != nil {
		log.Error(c, "Create media message failed, ", err)
		if err.Error() == constant.ErrForbiddenSendMessage {
			apihelper.AbortErrorHandle(c, common.GeneralForbidden)
			return
		}
		apihelper.AbortErrorHandle(c, common.GeneralServiceUnavailable)
		return
	}

	ch.wsManager.BroadcastToRoomExcept(0, roomID, &ws.WebSocketMessage{
		Type:    ws.MessageTypeNewMessage,
		Payload: message,
		RoomID:  &roomID,
	})
	apihelper.SuccessfulHandle(c, message)
}

func (ch *ChatController) SendMessage(c *gin.Context) {
	userIDStr, exists := c.Get("userID")
	if !exists {
		log.Error(c, "User ID not found in context")
		apihelper.AbortErrorHandle(c, common.GeneralUnauthorized)
		return
	}
	userID := userIDStr.(int64)

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
	req.SenderID = userID

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

	ch.wsManager.BroadcastToRoomExcept(0, roomID, &ws.WebSocketMessage{
		Type:    ws.MessageTypeNewMessage,
		Payload: message,
		RoomID:  &roomID,
	})

	apihelper.SuccessfulHandle(c, message)
}

func (ch *ChatController) MarkMessageAsRead(c *gin.Context) {
	messageID, err := strconv.ParseInt(c.Param("messageId"), 10, 64)
	if err != nil {
		log.Error(c, "Invalid message ID: %v", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	userIDFromContext, exists := c.Get("userID")
	if !exists {
		log.Error(c, "User ID not found in context")
		apihelper.AbortErrorHandle(c, common.GeneralUnauthorized)
		return
	}
	userID := userIDFromContext.(int64)

	roomID, err := strconv.ParseInt(c.Param("roomId"), 10, 64)
	if err != nil {
		log.Error(c, "Invalid room ID: %v", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	err = ch.chatRoomService.MarkMessageAsRead(c, roomID, userID, messageID)
	if err != nil {
		log.Error(c, "Failed to mark message as read: %v", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	apihelper.SuccessfulHandle(c, gin.H{"status": "message marked as read"})
}

func (ch *ChatController) CountUnreadMessages(c *gin.Context) {
	userIDFromContext, exists := c.Get("userID")
	if !exists {
		log.Error(c, "User ID not found in context")
		apihelper.AbortErrorHandle(c, common.GeneralUnauthorized)
		return
	}
	userID := userIDFromContext.(int64)

	roomID, err := strconv.ParseInt(c.Param("roomId"), 10, 64)
	if err != nil {
		log.Error(c, "Invalid room ID: %v", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	count, err := ch.chatRoomService.CountUnreadMessages(c, roomID, userID)
	if err != nil {
		log.Error(c, "Count unread messages failed: %v", err)
		apihelper.AbortErrorHandle(c, common.GeneralServiceUnavailable)
		return
	}

	apihelper.SuccessfulHandle(c, gin.H{"unreadCount": count})
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
