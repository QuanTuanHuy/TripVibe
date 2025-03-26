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
		chatV1.POST("/new_room", p.ChatController.CreateChatRoom)
		chatV1.POST("/:roomId/send_message", p.ChatController.SendMessage)
		chatV1.GET("/:roomId/messages", p.ChatController.GetMessagesByRoomID)
	}
}
