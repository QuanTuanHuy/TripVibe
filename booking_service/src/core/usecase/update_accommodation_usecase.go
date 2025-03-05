package usecase

import (
	"booking_service/core/domain/entity"
	"booking_service/core/port"
	"context"
	"github.com/golibs-starter/golib/log"
)

type IUpdateAccommodationUseCase interface {
	UpdateAccommodationByID(ctx context.Context, accommodation *entity.AccommodationEntity) (*entity.AccommodationEntity, error)
}

type UpdateAccommodationUseCase struct {
	accPort              port.IAccommodationPort
	unitPort             port.IUnitPort
	getAccUseCase        IGetAccommodationUseCase
	dbTransactionUseCase IDatabaseTransactionUseCase
}

func (u UpdateAccommodationUseCase) UpdateAccommodationByID(ctx context.Context, accommodation *entity.AccommodationEntity) (*entity.AccommodationEntity, error) {
	existedAcc, err := u.getAccUseCase.GetAccommodationByID(ctx, accommodation.ID)
	if err != nil {
		log.Error(ctx, "get accommodation by id failed", err)
		return nil, err
	}

	tx := u.dbTransactionUseCase.StartTransaction()
	defer func() {
		if errRollBack := u.dbTransactionUseCase.Rollback(tx); errRollBack != nil {
			log.Error(ctx, "Rollback update accommodation failed, : ", errRollBack)
		} else {
			log.Info(ctx, "Rollback update accommodation success")
		}
	}()

	err = u.unitPort.DeleteUnitsByAccID(ctx, tx, accommodation.ID)

	existedAcc, err = u.accPort.UpdateAccommodation(ctx, tx, accommodation)

	newUnits, err := u.unitPort.CreateUnits(ctx, tx, accommodation.Units)
	if err != nil {
		log.Error(ctx, "Create units failed, : ", err)
		return nil, err
	}
	existedAcc.Units = newUnits

	errCommit := u.dbTransactionUseCase.Commit(tx)
	if errCommit != nil {
		log.Error(ctx, "Commit update accommodation failed, : ", errCommit)
		return nil, errCommit
	}

	return existedAcc, nil
}

func NewUpdateAccommodationUseCase(accPort port.IAccommodationPort, unitPort port.IUnitPort, getAccUseCase IGetAccommodationUseCase, dbTransactionUseCase IDatabaseTransactionUseCase) IUpdateAccommodationUseCase {
	return &UpdateAccommodationUseCase{
		accPort:              accPort,
		unitPort:             unitPort,
		getAccUseCase:        getAccUseCase,
		dbTransactionUseCase: dbTransactionUseCase,
	}
}
