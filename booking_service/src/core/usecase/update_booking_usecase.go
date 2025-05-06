package usecase

import (
	"booking_service/core/domain/constant"
	"booking_service/core/domain/dto/request"
	"booking_service/core/domain/entity"
	"booking_service/core/port"
	"booking_service/kernel/utils"
	"context"
	"errors"
	"github.com/golibs-starter/golib/log"
)

type IUpdateBookingUseCase interface {
	ApproveBooking(ctx context.Context, userID, bookingID int64) error
	RejectBooking(ctx context.Context, userID, bookingID int64) error
}

type UpdateBookingUseCase struct {
	bookingPort          port.IBookingPort
	accommodationPort    port.IAccommodationPort
	userPort             port.IUserPort
	notificationPort     port.INotificationPort
	cachePort            port.ICachePort
	dbTransactionUseCase IDatabaseTransactionUseCase
}

func (u UpdateBookingUseCase) updateBookingStatusByAccOwner(ctx context.Context, userID, bookingID int64, newStatus string,
	errorType string, errorMsg string) error {

	booking, err := u.bookingPort.GetBookingByID(ctx, bookingID)
	if err != nil {
		log.Error(ctx, "GetBookingByID error", err)
		return err
	}

	if booking.Status != constant.PENDING {
		log.Error(ctx, "Booking status is not pending ", booking.Status)
		return errors.New(errorMsg)
	}

	accommodation, err := u.accommodationPort.GetAccommodationByID(ctx, booking.AccommodationID)
	if err != nil {
		log.Error(ctx, "GetAccommodationByID error", err)
		return err
	}

	if userID != accommodation.OwnerID {
		log.Error(ctx, "user doesn't have permission to "+errorType+" booking ", userID)
		return errors.New(errorMsg)
	}

	tx := u.dbTransactionUseCase.StartTransaction()
	defer func() {
		if errRollBack := u.dbTransactionUseCase.Rollback(tx); errRollBack != nil {
			log.Error(ctx, "Rollback "+errorType+" booking failed: ", errRollBack)
		} else {
			log.Info(ctx, "Rollback "+errorType+" booking success")
		}
	}()

	booking.Status = newStatus
	_, err = u.bookingPort.UpdateBooking(ctx, tx, booking)
	if err != nil {
		log.Error(ctx, "UpdateBooking error", err)
		return err
	}

	errCommit := u.dbTransactionUseCase.Commit(tx)
	if errCommit != nil {
		log.Error(ctx, "Commit "+errorType+" booking failed: ", errCommit)
		return errCommit
	}

	go func() {
		err := u.sendNotificationToTourist(ctx, booking)
		if err != nil {
			log.Error("ctx", "SendNotificationToTourist error", err)
		}

		err = u.cachePort.DeleteFromCache(ctx, utils.BuildCacheKeyGetBooking(bookingID))
		if err != nil {
			log.Error(ctx, "DeleteFromCache error", err)
		}
	}()

	return nil
}

// send notification to tourist
func (u UpdateBookingUseCase) sendNotificationToTourist(ctx context.Context, booking *entity.BookingEntity) error {
	tourist, err := u.userPort.GetUserByID(ctx, booking.TouristID)
	if err != nil || tourist == nil {
		log.Error(ctx, "GetUserByID error", err)
		return err
	}

	notification := &request.CreateNotificationDto{
		UserID:    tourist.ID,
		Type:      "EMAIL",
		Title:     "Your booking has been " + booking.Status,
		Content:   "Your booking has been " + booking.Status + " by accommodation owner",
		Recipient: tourist.Email,
	}

	err = u.notificationPort.SendNotification(ctx, notification)
	if err != nil {
		log.Error(ctx, "SendNotification error", err)
		return err
	}
	return nil
}

func (u UpdateBookingUseCase) ApproveBooking(ctx context.Context, userID, bookingID int64) error {
	return u.updateBookingStatusByAccOwner(ctx, userID, bookingID, constant.APPROVED, "approve",
		constant.ErrForbiddenApprovedBooking)
}

func (u UpdateBookingUseCase) RejectBooking(ctx context.Context, userID, bookingID int64) error {
	return u.updateBookingStatusByAccOwner(ctx, userID, bookingID, constant.REJECTED, "reject",
		constant.ErrForbiddenRejectBooking)
	// need to remove data in search service
}

func NewUpdateBookingUseCase(bookingPort port.IBookingPort,
	dbTransactionUseCase IDatabaseTransactionUseCase,
	userPort port.IUserPort,
	cachePort port.ICachePort,
	accommodationPort port.IAccommodationPort,
	notificationPort port.INotificationPort) IUpdateBookingUseCase {
	return &UpdateBookingUseCase{
		bookingPort:          bookingPort,
		cachePort:            cachePort,
		notificationPort:     notificationPort,
		accommodationPort:    accommodationPort,
		userPort:             userPort,
		dbTransactionUseCase: dbTransactionUseCase,
	}
}
