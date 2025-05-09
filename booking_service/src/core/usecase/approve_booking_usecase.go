package usecase

import (
	"booking_service/core/domain/constant"
	"booking_service/core/port"
	"booking_service/kernel/utils"
	"context"
	"errors"

	"github.com/golibs-starter/golib/log"
)

type IApproveBookingUseCase interface {
	ApproveBooking(ctx context.Context, bookingID int64, userID int64) error
}

type ApproveBookingUseCase struct {
	bookingPort          port.IBookingPort
	accommodationPort    port.IAccommodationPort
	cachePort            port.ICachePort
	sendEmailUseCase     ISendEmailUseCase
	dbTransactionUseCase IDatabaseTransactionUseCase
}

func (a *ApproveBookingUseCase) ApproveBooking(ctx context.Context, bookingID int64, userID int64) error {
	booking, err := a.bookingPort.GetBookingByID(ctx, bookingID)
	if err != nil {
		log.Error(ctx, "GetBookingByID error", err)
		return err
	}

	if booking.Status != constant.PENDING {
		log.Error(ctx, "Booking status is not pending ", booking.Status)
		return errors.New(constant.ErrForbiddenApprovedBooking)
	}

	accommodation, err := a.accommodationPort.GetAccommodationByID(ctx, booking.AccommodationID)
	if err != nil {
		log.Error(ctx, "GetAccommodationByID error", err)
		return err
	}

	if userID != accommodation.OwnerID {
		log.Error(ctx, "user doesn't have permission to approve booking ", userID)
		return errors.New(constant.ErrForbiddenApprovedBooking)
	}

	tx := a.dbTransactionUseCase.StartTransaction()
	defer func() {
		if errRollBack := a.dbTransactionUseCase.Rollback(tx); errRollBack != nil {
			log.Error(ctx, "Rollback approve booking failed: ", errRollBack)
		} else {
			log.Info(ctx, "Rollback approve booking success")
		}
	}()

	booking.Status = constant.APPROVED
	_, err = a.bookingPort.UpdateBooking(ctx, tx, booking)
	if err != nil {
		log.Error(ctx, "UpdateBooking error", err)
		return err
	}

	errCommit := a.dbTransactionUseCase.Commit(tx)
	if errCommit != nil {
		log.Error(ctx, "Commit approve booking failed: ", errCommit)
		return errCommit
	}

	go func() {
		err := a.sendEmailUseCase.SendBookingEmail(ctx, bookingID,
			"Your booking has been approved",
			"Your booking has been approved by the owner. Please check your booking details.")
		if err != nil {
			log.Error("ctx", "SendNotificationToTourist error", err)
		}

		err = a.cachePort.DeleteFromCache(ctx, utils.BuildCacheKeyGetBooking(bookingID))
		if err != nil {
			log.Error(ctx, "DeleteFromCache error", err)
		}
	}()

	return nil
}

func NewApproveBookingUseCase(
	bookingPort port.IBookingPort,
	accommodationPort port.IAccommodationPort,
	cachePort port.ICachePort,
	sendEmailUseCase ISendEmailUseCase,
	dbTransactionUseCase IDatabaseTransactionUseCase,
) IApproveBookingUseCase {
	return &ApproveBookingUseCase{
		bookingPort:          bookingPort,
		accommodationPort:    accommodationPort,
		cachePort:            cachePort,
		sendEmailUseCase:     sendEmailUseCase,
		dbTransactionUseCase: dbTransactionUseCase,
	}
}
