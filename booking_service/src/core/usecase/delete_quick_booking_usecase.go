package usecase

import (
	"booking_service/core/port"
	"context"
	"github.com/golibs-starter/golib/log"
)

type IDeleteQuickBookingUseCase interface {
	DeleteQuickBooking(ctx context.Context, userID int64, id int64) error
}

type DeleteQuickBookingUseCase struct {
	quickBookingPort     port.IQuickBookingPort
	dbTransactionUseCase IDatabaseTransactionUseCase
}

func (d DeleteQuickBookingUseCase) DeleteQuickBooking(ctx context.Context, userID int64, id int64) error {
	var err error
	tx := d.dbTransactionUseCase.StartTransaction()

	defer func() {
		if err = d.dbTransactionUseCase.Rollback(tx); err != nil {
			log.Error(ctx, "Error rolling back transaction", err)
		} else {
			log.Error(ctx, "Transaction rolled back successfully")
		}
	}()

	if err = d.quickBookingPort.DeleteQuickBooking(ctx, tx, id, userID); err != nil {
		log.Error(ctx, "Error deleting quick booking", err)
		return err
	}

	if err = d.dbTransactionUseCase.Commit(tx); err != nil {
		log.Error(ctx, "Error committing transaction", err)
		return err
	}

	return nil
}

func NewDeleteQuickBookingUseCase(
	quickBookingPort port.IQuickBookingPort,
	trx IDatabaseTransactionUseCase,
) IDeleteQuickBookingUseCase {
	return &DeleteQuickBookingUseCase{
		quickBookingPort:     quickBookingPort,
		dbTransactionUseCase: trx,
	}
}
