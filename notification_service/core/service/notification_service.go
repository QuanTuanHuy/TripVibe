package service

import (
	"context"
	"github.com/golibs-starter/golib/log"
	"notification_service/core/domain/dto/request"
	"notification_service/core/domain/dto/response"
	"notification_service/core/domain/entity"
	"notification_service/core/usecase"
	"notification_service/kernel/utils"
)

type INotificationService interface {
	CreateNotification(ctx context.Context, notification *entity.NotificationEntity) (*entity.NotificationEntity, error)
	GetAllNotification(ctx context.Context, userID int64, params *request.NotificationParams) (*response.GetNotificationResponse, error)
}

type NotificationService struct {
	createNotificationUseCase usecase.ICreateNotificationUseCase
	getNotificationUseCase    usecase.IGetNotificationUseCase
}

func (n *NotificationService) GetAllNotification(ctx context.Context, userID int64, params *request.NotificationParams) (*response.GetNotificationResponse, error) {
	notifications, err := n.getNotificationUseCase.GetAllNotification(ctx, userID, params)
	if err != nil {
		return nil, err
	}
	log.Info(ctx, "notifications length: ", len(notifications))

	total, err := n.getNotificationUseCase.CountAllNotification(ctx, userID, params)
	if err != nil {
		return nil, err
	}
	log.Info(ctx, "total: ", total)

	page := int64(*params.Page)
	pageSize := int64(*params.PageSize)
	log.Info(ctx, "page: ", page)
	log.Info(ctx, "pageSize: ", pageSize)
	nextPage, prevPage, totalPage := utils.CalculateParameterForGetRequest(page, pageSize, total)
	return response.ToGetNotificationResponse(notifications, page, pageSize, totalPage, total, prevPage, nextPage), nil

}

func (n *NotificationService) CreateNotification(ctx context.Context, notification *entity.NotificationEntity) (*entity.NotificationEntity, error) {
	return n.createNotificationUseCase.CreateNotification(ctx, notification)
}

func NewNotificationService(createNotificationUseCase usecase.ICreateNotificationUseCase,
	getNotificationUseCase usecase.IGetNotificationUseCase) INotificationService {
	return &NotificationService{
		createNotificationUseCase: createNotificationUseCase,
		getNotificationUseCase:    getNotificationUseCase,
	}
}
