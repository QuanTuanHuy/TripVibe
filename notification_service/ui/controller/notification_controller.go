package controller

import (
	"github.com/gin-gonic/gin"
	"github.com/golibs-starter/golib/log"
	"notification_service/core/common"
	"notification_service/core/domain/dto/request"
	"notification_service/core/service"
	"notification_service/kernel/apihelper"
	"notification_service/kernel/utils"
	"strconv"
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

func (n *NotificationController) GetAllNotification(c *gin.Context) {
	userID, err := strconv.ParseInt(c.Query("userId"), 10, 64)
	if err != nil {
		log.Error(c, "Parse userID failed: ", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	pageSize, page := utils.GetPagingParams(c)
	notiParams := &request.NotificationParams{}
	notiParams.Page = &page
	notiParams.PageSize = &pageSize

	notifications, err := n.notificationService.GetAllNotification(c, userID, notiParams)
	if err != nil {
		log.Error(c, "Get all notification failed: ", err)
		apihelper.AbortErrorHandle(c, common.GeneralServiceUnavailable)
		return
	}

	apihelper.SuccessfulHandle(c, notifications)
}

func (n *NotificationController) UpdateNotification(c *gin.Context) {
	// get notification id
	notiID, err := strconv.ParseInt(c.Param("id"), 10, 64)
	if err != nil {
		log.Error(c, "Parse notification ID failed: ", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	// get request body
	var req request.UpdateNotificationRequestDto
	if err := c.ShouldBindJSON(&req); err != nil {
		log.Error(c, "Bind request failed: ", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	notification, err := n.notificationService.UpdateNotification(c, notiID, &req)
	if err != nil {
		log.Error(c, "Update notification failed: ", err)
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
