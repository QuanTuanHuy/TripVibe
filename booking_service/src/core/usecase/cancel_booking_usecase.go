package usecase

import (
	"booking_service/core/domain/constant"
	"booking_service/core/port"
	"context"
	"github.com/golibs-starter/golib/log"
)

type ICancelBookingUseCase interface {
	CancelBooking(ctx context.Context, userID, bookingID int64) error
}

type CancelBookingUseCase struct {
	bookingPort          port.IBookingPort
	inventoryPort        port.IInventoryPort
	dbTransactionUseCase IDatabaseTransactionUseCase
}

func (c CancelBookingUseCase) CancelBooking(ctx context.Context, userID, bookingID int64) error {
	booking, err := c.bookingPort.GetBookingByID(ctx, bookingID)
	if err != nil {
		log.Error(ctx, "get booking by id failed", err)
		return err
	}
	if booking.TouristID != userID {
		log.Error(ctx, "user not allowed to cancel booking", err)
		return err
	}
	if booking.Status != constant.PENDING {
		log.Error(ctx, "booking not pending", err)
		return err
	}

	tx := c.dbTransactionUseCase.StartTransaction()
	defer c.dbTransactionUseCase.Rollback(tx)

	booking.Status = constant.CANCELLED
	if _, err := c.bookingPort.UpdateBooking(ctx, tx, booking); err != nil {
		log.Error(ctx, "update booking failed", err)
		return err
	}

	// release inventory
	if err := c.inventoryPort.ReleaseBooking(ctx, tx, bookingID); err != nil {
		log.Error(ctx, "release booking failed", err)
		return err
	}

	if err := c.dbTransactionUseCase.Commit(tx); err != nil {
		log.Error(ctx, "commit transaction failed", err)
		return err
	}

	return nil
}

func NewCancelBookingUseCase(
	bookingPort port.IBookingPort,
	inventoryPort port.IInventoryPort,
	dbTransactionUseCase IDatabaseTransactionUseCase,
) ICancelBookingUseCase {
	return &CancelBookingUseCase{
		bookingPort:          bookingPort,
		inventoryPort:        inventoryPort,
		dbTransactionUseCase: dbTransactionUseCase,
	}
}
