package service

import (
	"context"
	"notification_service/core/domain/dto/request"
	"notification_service/core/domain/dto/response"
	"notification_service/core/domain/entity"
	"notification_service/core/usecase"
	"notification_service/kernel/utils"
)

type INotificationService interface {
	CreateNotification(ctx context.Context, notification *entity.NotificationEntity) (*response.NotificationResponse, error)
	GetAllNotification(ctx context.Context, userID int64, params *request.NotificationParams) (*response.GetNotificationResponse, error)
	UpdateNotification(ctx context.Context, notiID int64, req *request.UpdateNotificationRequestDto) (*response.NotificationResponse, error)
}

type NotificationService struct {
	createNotificationUseCase usecase.ICreateNotificationUseCase
	getNotificationUseCase    usecase.IGetNotificationUseCase
	updateNotificationUseCase usecase.IUpdateNotificationUseCase
	emailNotificationUseCase  usecase.IEmailNotificationUseCase
}

func (n *NotificationService) UpdateNotification(ctx context.Context, notiID int64, req *request.UpdateNotificationRequestDto) (*response.NotificationResponse, error) {
	notification, err := n.updateNotificationUseCase.UpdateNotification(ctx, notiID, req)
	if err != nil {
		return nil, err
	}
	return response.ToNotificationResponse(notification), nil
}

func (n *NotificationService) GetAllNotification(ctx context.Context, userID int64, params *request.NotificationParams) (*response.GetNotificationResponse, error) {
	notifications, err := n.getNotificationUseCase.GetAllNotification(ctx, userID, params)
	if err != nil {
		return nil, err
	}

	total, err := n.getNotificationUseCase.CountAllNotification(ctx, userID, params)
	if err != nil {
		return nil, err
	}

	page := int64(*params.Page)
	pageSize := int64(*params.PageSize)
	nextPage, prevPage, totalPage := utils.CalculateParameterForGetRequest(page, pageSize, total)
	return response.ToGetNotificationResponse(notifications, page, pageSize, totalPage, total, prevPage, nextPage), nil
}

func (n *NotificationService) CreateNotification(ctx context.Context, notification *entity.NotificationEntity) (*response.NotificationResponse, error) {
	// First, persist the notification
	notification, err := n.createNotificationUseCase.CreateNotification(ctx, notification)
	if err != nil {
		return nil, err
	}

	// If it's an email notification, queue it for processing
	if notification.Type == entity.EmailNotification {
		err = n.emailNotificationUseCase.QueueEmailNotification(ctx, notification)
		if err != nil {
			// Note: We don't return the error here as the notification was already persisted
			// The notification will be in a PENDING state and can be retried later
		}
	}

	return response.ToNotificationResponse(notification), nil
}

func NewNotificationService(
	createNotificationUseCase usecase.ICreateNotificationUseCase,
	getNotificationUseCase usecase.IGetNotificationUseCase,
	updateNotificationUseCase usecase.IUpdateNotificationUseCase,
	emailNotificationUseCase usecase.IEmailNotificationUseCase,
) INotificationService {
	return &NotificationService{
		createNotificationUseCase: createNotificationUseCase,
		getNotificationUseCase:    getNotificationUseCase,
		updateNotificationUseCase: updateNotificationUseCase,
		emailNotificationUseCase:  emailNotificationUseCase,
	}
}
