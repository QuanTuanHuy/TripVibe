package controller

import (
	"chat_service/core/domain/common"
	"chat_service/core/domain/dto"
	"chat_service/core/service"
	"chat_service/kernel/apihelper"
	"github.com/gin-gonic/gin"
	"github.com/golibs-starter/golib/log"
)

type ChatController struct {
	chatRoomService service.IChatRoomService
}

func NewChatController(chatRoomService service.IChatRoomService) *ChatController {
	return &ChatController{chatRoomService: chatRoomService}
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
