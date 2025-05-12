package usecase

import (
	"booking_service/core/domain/constant"
	"booking_service/core/domain/dto/request"
	"booking_service/core/domain/entity"
	"booking_service/core/port"
	"context"
	"encoding/json"
	"errors"
	"github.com/golibs-starter/golib/log"
	"slices"
)

type ICreateQuickBookingUseCase interface {
	CreateQuickBooking(ctx context.Context, userID int64, req *request.CreateQuickBookingDto) (*entity.QuickBookingEntity, error)
}

type CreateQuickBookingUseCase struct {
	quickBookingPort     port.IQuickBookingPort
	dbTransactionUseCase IDatabaseTransactionUseCase
	getAccUseCase        IGetAccommodationUseCase
}

func (q CreateQuickBookingUseCase) CreateQuickBooking(ctx context.Context, userID int64, req *request.CreateQuickBookingDto) (*entity.QuickBookingEntity, error) {
	accommodation, err := q.getAccUseCase.GetAccommodationByID(ctx, req.AccommodationID)
	if err != nil {
		log.Error(ctx, "GetAccommodationByID error ", err)
		return nil, err
	}
	allUnitIDs := make([]int64, 0)
	for _, unit := range accommodation.Units {
		allUnitIDs = append(allUnitIDs, unit.ID)
	}
	for _, unitID := range req.PreferredUnitIDs {
		if !slices.Contains(allUnitIDs, unitID) {
			log.Error(ctx, "Unit not found ", unitID)
			return nil, errors.New(constant.ErrUnitNotBelongToAccommodation)
		}
	}

	preferredUnitIDsJSON, err := json.Marshal(req.PreferredUnitIDs)
	if err != nil {
		log.Error(ctx, "Error marshaling preferred unit IDs", err)
		return nil, err
	}

	quickBookingEntity := &entity.QuickBookingEntity{
		UserID:           userID,
		Name:             req.Name,
		AccommodationID:  req.AccommodationID,
		NumberOfAdult:    req.NumberOfAdult,
		NumberOfChild:    req.NumberOfChild,
		PreferredUnitIDs: string(preferredUnitIDsJSON),
	}

	tx := q.dbTransactionUseCase.StartTransaction()
	defer func() {
		if err = q.dbTransactionUseCase.Rollback(tx); err != nil {
			log.Error(ctx, "Error rolling back transaction", err)
		} else {
			log.Error(ctx, "Transaction rolled back successfully")
		}
	}()

	createdEntity, err := q.quickBookingPort.CreateQuickBooking(ctx, tx, quickBookingEntity)
	if err != nil {
		log.Error(ctx, "Error creating quick booking", err)
		return nil, err
	}

	if err = q.dbTransactionUseCase.Commit(tx); err != nil {
		log.Error(ctx, "Error committing transaction", err)
		return nil, err
	}

	return createdEntity, nil
}

func NewCreateQuickBookingUseCase(quickBookingPort port.IQuickBookingPort,
	dbTransactionUseCase IDatabaseTransactionUseCase,
	getAccUseCase IGetAccommodationUseCase) ICreateQuickBookingUseCase {
	return &CreateQuickBookingUseCase{
		quickBookingPort:     quickBookingPort,
		dbTransactionUseCase: dbTransactionUseCase,
		getAccUseCase:        getAccUseCase,
	}
}
