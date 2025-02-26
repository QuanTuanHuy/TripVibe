package controller

import (
	"github.com/gin-gonic/gin"
	"github.com/golibs-starter/golib/log"
	"notification_service/core/common"
	"notification_service/core/domain/dto/request"
	"notification_service/core/service"
	"notification_service/kernel/apihelper"
)

type NotificationController struct {
	notificationService service.INotificationService
}

func (n *NotificationController) CreateNotification(c *gin.Context) {
	var req request.CreateNotificationRequestDto
	if err := c.ShouldBindJSON(&req); err != nil {
		log.Error(c, "Bind request failed: ", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	notification, err := n.notificationService.CreateNotification(c, request.ToNotificationEntity(&req))
	if err != nil {
		log.Error(c, "Create notification failed: ", err)
		apihelper.AbortErrorHandle(c, common.GeneralServiceUnavailable)
		return
	}

	apihelper.SuccessfulHandle(c, notification)
}

func NewNotificationController(notificationService service.INotificationService) *NotificationController {
	return &NotificationController{
		notificationService: notificationService,
	}
}
