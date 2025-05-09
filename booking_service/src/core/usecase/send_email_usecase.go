package usecase

import (
	"booking_service/core/domain/dto/request"
	"booking_service/core/port"
	"context"
	"github.com/golibs-starter/golib/log"
)

type ISendEmailUseCase interface {
	SendBookingEmail(ctx context.Context, bookingID int64, title, content string) error
}

type SendEmailUseCase struct {
	userPort         port.IUserPort
	bookingPort      port.IBookingPort
	notificationPort port.INotificationPort
}

func (s SendEmailUseCase) SendBookingEmail(ctx context.Context, bookingID int64, title, content string) error {
	booking, err := s.bookingPort.GetBookingByID(ctx, bookingID)
	if err != nil {
		log.Error(ctx, "get booking by id failed", err)
		return err
	}

	user, err := s.userPort.GetUserByID(ctx, booking.TouristID)
	if err != nil {
		log.Error(ctx, "get user by id failed", err)
		return err
	}

	notification := &request.CreateNotificationDto{
		UserID:    user.ID,
		Type:      "EMAIL",
		Title:     title,
		Content:   content,
		Recipient: user.Email,
	}

	err = s.notificationPort.SendNotification(ctx, notification)
	if err != nil {
		log.Error(ctx, "send notification failed", err)
		return err
	}
	return nil
}

func NewSendEmailUseCase(
	userPort port.IUserPort,
	bookingPort port.IBookingPort,
	notificationPort port.INotificationPort,
) ISendEmailUseCase {
	return &SendEmailUseCase{
		userPort:         userPort,
		bookingPort:      bookingPort,
		notificationPort: notificationPort,
	}
}
