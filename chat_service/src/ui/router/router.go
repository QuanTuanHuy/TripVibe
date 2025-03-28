package router

import (
	"chat_service/ui/controller"
	"github.com/gin-gonic/gin"
	"github.com/golibs-starter/golib"
	"go.uber.org/fx"
)

type RegisterRoutersIn struct {
	fx.In
	App            *golib.App
	Engine         *gin.Engine
	ChatController *controller.ChatController
}

func RegisterGinRouters(p RegisterRoutersIn) {
	router := p.Engine.Group(p.App.Path())

	chatV1 := router.Group("/public/v1/chats")
	{
		chatV1.POST("/rooms", p.ChatController.CreateChatRoom)
		chatV1.POST("/rooms/:roomId/send_message", p.ChatController.SendMessage)
		chatV1.GET("/rooms/:roomId/messages", p.ChatController.GetMessagesByRoomID)
		chatV1.GET("/rooms", p.ChatController.GetChatRooms)
		chatV1.POST("/rooms/:roomId/messages/:messageId/read", p.ChatController.MarkMessageAsRead)
		chatV1.GET("/rooms/:roomId/unread", p.ChatController.CountUnreadMessages)
	}
}
