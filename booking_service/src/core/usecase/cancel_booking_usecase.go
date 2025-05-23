package usecase

import (
	"booking_service/core/domain/constant"
	"booking_service/core/domain/dto/request"
	"booking_service/core/port"
	"booking_service/kernel/utils"
	"context"
	"errors"
	"github.com/golibs-starter/golib/log"
)

type ICancelBookingUseCase interface {
	CancelBooking(ctx context.Context, userID, bookingID int64) error
}

type CancelBookingUseCase struct {
	bookingPort          port.IBookingPort
	inventoryPort        port.IInventoryPort
	cachePort            port.ICachePort
	dbTransactionUseCase IDatabaseTransactionUseCase
}

func (c CancelBookingUseCase) CancelBooking(ctx context.Context, userID, bookingID int64) error {
	booking, err := c.bookingPort.GetBookingByID(ctx, bookingID)
	if err != nil {
		return err
	}

	if booking.TouristID != userID {
		log.Error(ctx, "user not allowed to cancel booking", err)
		return errors.New(constant.ErrForbiddenCancelBooking)
	}
	if booking.Status != constant.CONFIRMED && booking.Status != constant.APPROVED {
		log.Error(ctx, "booking not allowed to cancel", err)
		return errors.New(constant.ErrForbiddenCancelBooking)
	}

	// release inventories
	cancelRequest := request.CancelBookingRequest{
		BookingID: bookingID,
		UserID:    userID,
	}
	log.Info(ctx, "booking cancel request: ", cancelRequest)
	cancelResponse, err := c.inventoryPort.CancelBooking(ctx, &cancelRequest)
	if err != nil {
		log.Error(ctx, "cancel booking err: ", err)
		return err
	}
	if !cancelResponse.Success {
		log.Error(ctx, "cancel booking failed: ", cancelResponse.Errors)
		return errors.New(constant.ErrCancelBookingFailed)
	}

	tx := c.dbTransactionUseCase.StartTransaction()
	defer func() {
		if errRollBack := c.dbTransactionUseCase.Rollback(tx); errRollBack != nil {
			log.Error(ctx, "Rollback cancel booking failed: ", errRollBack)
		} else {
			log.Info(ctx, "Rollback cancel booking success")
		}
	}()

	booking.Status = constant.CANCELLED
	if _, err := c.bookingPort.UpdateBooking(ctx, tx, booking); err != nil {
		log.Error(ctx, "update booking failed", err)
		return err
	}

	if err := c.dbTransactionUseCase.Commit(tx); err != nil {
		log.Error(ctx, "commit transaction failed", err)
		return err
	}

	go func() {
		err = c.cachePort.DeleteFromCache(ctx, utils.BuildCacheKeyGetBooking(bookingID))
		if err != nil {
			log.Error(ctx, "DeleteFromCache error", err)
		}
	}()

	return nil
}

func NewCancelBookingUseCase(
	bookingPort port.IBookingPort,
	inventoryPort port.IInventoryPort,
	cachePort port.ICachePort,
	dbTransactionUseCase IDatabaseTransactionUseCase,
) ICancelBookingUseCase {
	return &CancelBookingUseCase{
		bookingPort:          bookingPort,
		inventoryPort:        inventoryPort,
		cachePort:            cachePort,
		dbTransactionUseCase: dbTransactionUseCase,
	}
}
