package usecase

import (
	"booking_service/core/domain/dto/request"
	"booking_service/core/domain/entity"
	"booking_service/core/port"
	"context"
	"encoding/json"
	"github.com/golibs-starter/golib/log"
)

type IUpdateQuickBookingUseCase interface {
	UpdateQuickBooking(ctx context.Context, userID int64, id int64, req *request.UpdateQuickBookingDto) (*entity.QuickBookingEntity, error)
}

type UpdateQuickBookingUseCase struct {
	quickBookingPort     port.IQuickBookingPort
	dbTransactionUseCase IDatabaseTransactionUseCase
	getAccUseCase        IGetAccommodationUseCase
}

func (u UpdateQuickBookingUseCase) UpdateQuickBooking(ctx context.Context, userID int64, id int64, req *request.UpdateQuickBookingDto) (*entity.QuickBookingEntity, error) {
	existingBooking, err := u.quickBookingPort.GetQuickBooking(ctx, userID, id)
	if err != nil {
		return nil, err
	}

	if req.Name != nil {
		existingBooking.Name = *req.Name
	}

	if req.AccommodationID != nil {
		existingBooking.AccommodationID = *req.AccommodationID
	}

	if req.NumberOfAdult != nil {
		existingBooking.NumberOfAdult = *req.NumberOfAdult
	}

	if req.NumberOfChild != nil {
		existingBooking.NumberOfChild = *req.NumberOfChild
	}

	if req.PreferredUnitIDs != nil {
		preferredUnitIDsJSON, err := json.Marshal(req.PreferredUnitIDs)
		if err != nil {
			log.Error(ctx, "Error marshaling preferred unit IDs", err)
			return nil, err
		}
		existingBooking.PreferredUnitIDs = string(preferredUnitIDsJSON)
	}

	tx := u.dbTransactionUseCase.StartTransaction()
	defer func() {
		if err = u.dbTransactionUseCase.Rollback(tx); err != nil {
			log.Error(ctx, "Error rolling back transaction", err)
		} else {
			log.Error(ctx, "Transaction rolled back successfully")
		}
	}()

	updatedEntity, err := u.quickBookingPort.UpdateQuickBooking(ctx, tx, existingBooking)
	if err != nil {
		log.Error(ctx, "Error updating quick booking", err)
		return nil, err
	}

	if err = u.dbTransactionUseCase.Commit(tx); err != nil {
		log.Error(ctx, "Error committing transaction", err)
		return nil, err
	}

	return updatedEntity, nil
}

func NewUpdateQuickBookingUseCase(
	quickBookingPort port.IQuickBookingPort,
	trx IDatabaseTransactionUseCase,
	getAccUseCase IGetAccommodationUseCase,
) IUpdateQuickBookingUseCase {
	return &UpdateQuickBookingUseCase{
		quickBookingPort:     quickBookingPort,
		dbTransactionUseCase: trx,
		getAccUseCase:        getAccUseCase,
	}
}
