package usecase

import (
	"booking_service/core/domain/constant"
	"booking_service/core/port"
	"booking_service/kernel/utils"
	"context"
	"errors"

	"github.com/golibs-starter/golib/log"
)

type IRejectBookingUseCase interface {
	RejectBooking(ctx context.Context, userID, bookingID int64) error
}

type RejectBookingUseCase struct {
	bookingPort             port.IBookingPort
	inventoryPort           port.IInventoryPort
	cachePort               port.ICachePort
	getAccommodationUseCase IGetAccommodationUseCase
	dbTransactionUseCase    IDatabaseTransactionUseCase
	sendEmailUseCase        ISendEmailUseCase
}

func (r RejectBookingUseCase) RejectBooking(ctx context.Context, userID, bookingID int64) error {
	booking, err := r.bookingPort.GetBookingByID(ctx, bookingID)
	if err != nil {
		return err
	}
	if booking.Status != constant.CONFIRMED {
		log.Error(ctx, "Booking status is not confirmed ", booking.Status)
		return errors.New(constant.ErrForbiddenRejectBooking)
	}

	accommodation, err := r.getAccommodationUseCase.GetAccommodationByID(ctx, booking.AccommodationID)
	if err != nil {
		return err
	}
	if userID != accommodation.OwnerID {
		log.Error(ctx, "user doesn't have permission to reject booking ", userID)
		return errors.New(constant.ErrForbiddenRejectBooking)
	}

	tx := r.dbTransactionUseCase.StartTransaction()
	booking.Status = constant.REJECTED
	_, err = r.bookingPort.UpdateBooking(ctx, tx, booking)
	if err != nil {
		log.Error(ctx, "UpdateBooking error", err)
		return err
	}

	errCommit := r.dbTransactionUseCase.Commit(tx)
	if errCommit != nil {
		log.Error(ctx, "Commit reject booking failed: ", errCommit)
		return errCommit
	}

	go func() {
		err = r.sendEmailUseCase.SendBookingEmail(ctx, bookingID,
			"Your booking has been rejected",
			"Your booking has been rejected by the owner. Please check your booking status for more details.")

		err = r.cachePort.DeleteFromCache(ctx, utils.BuildCacheKeyGetBooking(bookingID))
		if err != nil {
			log.Error(ctx, "DeleteFromCache error", err)
		}

		err = r.inventoryPort.ReleaseLock(ctx, *booking.InventoryLockID)
		if err != nil {
			log.Error(ctx, "ReleaseLock error", err)
		}
	}()

	return nil
}

func NewRejectBookingUseCase(
	bookingPort port.IBookingPort,
	inventoryPort port.IInventoryPort,
	getAccommodationUseCase IGetAccommodationUseCase,
	cachePort port.ICachePort,
	dbTransactionUseCase IDatabaseTransactionUseCase,
	sendEmailUseCase ISendEmailUseCase,
) IRejectBookingUseCase {
	return &RejectBookingUseCase{
		bookingPort:             bookingPort,
		inventoryPort:           inventoryPort,
		getAccommodationUseCase: getAccommodationUseCase,
		cachePort:               cachePort,
		dbTransactionUseCase:    dbTransactionUseCase,
		sendEmailUseCase:        sendEmailUseCase,
	}
}
