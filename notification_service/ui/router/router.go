package router

import (
	"github.com/gin-gonic/gin"
	"github.com/golibs-starter/golib"
	"go.uber.org/fx"
	"notification_service/ui/controller"
)

type RegisterRoutersIn struct {
	fx.In
	App                    *golib.App
	Engine                 *gin.Engine
	NotificationController *controller.NotificationController
}

func RegisterGinRouters(p RegisterRoutersIn) {
	router := p.Engine.Group(p.App.Path())
	notificationV1 := router.Group("public/v1/notifications")
	{
		notificationV1.POST("", p.NotificationController.CreateNotification)
		notificationV1.GET("", p.NotificationController.GetAllNotification)
		notificationV1.PUT("/:id", p.NotificationController.UpdateNotification)
	}
}
