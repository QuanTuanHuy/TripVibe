package usecase

import (
	"booking_service/core/domain/constant"
	"booking_service/core/domain/dto/request"
	"booking_service/core/domain/dto/response"
	"booking_service/core/port"
	"booking_service/kernel/utils"
	"context"
	"errors"
	"github.com/golibs-starter/golib/log"
)

type IConfirmBookingUseCase interface {
	ConfirmBooking(ctx context.Context, bookingID int64) (*response.ConfirmBookingResponse, error)
}

type ConfirmBookingUseCase struct {
	bookingPort          port.IBookingPort
	inventoryPort        port.IInventoryPort
	dbTransactionUseCase IDatabaseTransactionUseCase
	cachePort            port.ICachePort
}

func (c ConfirmBookingUseCase) ConfirmBooking(ctx context.Context, bookingID int64) (*response.ConfirmBookingResponse, error) {
	booking, err := c.bookingPort.GetBookingByID(ctx, bookingID)
	if err != nil {
		log.Error(ctx, "GetBookingByID error", err)
		return nil, err
	}
	if booking.Status != constant.PENDING {
		log.Error(ctx, "Booking status is not pending ", booking.Status)
		return nil, errors.New(constant.ErrForbiddenConfirmBooking)
	}

	tx := c.dbTransactionUseCase.StartTransaction()
	defer func() {
		if errRollBack := c.dbTransactionUseCase.Rollback(tx); errRollBack != nil {
			log.Error(ctx, "Rollback approve booking failed: ", errRollBack)
		} else {
			log.Info(ctx, "Rollback approve booking success")
		}
	}()

	booking.Status = constant.CONFIRMED
	if booking, err = c.bookingPort.UpdateBooking(ctx, tx, booking); err != nil {
		log.Error(ctx, "UpdateBooking error", err)
		return nil, err
	}

	inventoryConfirm := request.ConfirmBookingRequest{
		BookingID: booking.ID,
		LockID:    *booking.InventoryLockID,
	}
	confirmResponse, err := c.inventoryPort.ConfirmBooking(ctx, &inventoryConfirm)
	if err != nil {
		log.Error(ctx, "ConfirmBooking error", err)
		return nil, err
	}
	if !confirmResponse.Success {
		log.Error(ctx, "Can't confirm booking in inventory service, ", err)
		return confirmResponse, errors.New(confirmResponse.Errors[0])
	}

	errCommit := c.dbTransactionUseCase.Commit(tx)
	if errCommit != nil {
		log.Error(ctx, "Commit approve booking failed: ", errCommit)
		return nil, errCommit
	}

	go func() {
		err := c.cachePort.DeleteFromCache(ctx, utils.BuildCacheKeyGetBooking(bookingID))
		if err != nil {
			log.Error(ctx, "DeleteFromCache error", err)
		}
	}()

	return confirmResponse, nil
}

func NewConfirmBookingUseCase(
	bookingPort port.IBookingPort,
	inventoryPort port.IInventoryPort,
	dbTransactionUseCase IDatabaseTransactionUseCase,
	cachePort port.ICachePort,
) IConfirmBookingUseCase {
	return &ConfirmBookingUseCase{
		bookingPort:          bookingPort,
		inventoryPort:        inventoryPort,
		dbTransactionUseCase: dbTransactionUseCase,
		cachePort:            cachePort,
	}
}
