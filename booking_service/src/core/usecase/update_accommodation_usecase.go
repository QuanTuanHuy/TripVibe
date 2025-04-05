package usecase

import (
	"booking_service/core/domain/dto/request"
	"booking_service/core/domain/entity"
	"booking_service/core/port"
	"booking_service/kernel/utils"
	"context"
	"github.com/golibs-starter/golib/log"
)

type IUpdateAccommodationUseCase interface {
	UpdateAccommodationByID(ctx context.Context, accommodation *entity.AccommodationEntity) (*entity.AccommodationEntity, error)
	AddUnitToAccommodation(ctx context.Context, req *request.AddUnitToAccommodationDto) (*entity.UnitEntity, error)
}

type UpdateAccommodationUseCase struct {
	accPort              port.IAccommodationPort
	unitPort             port.IUnitPort
	cachePort            port.ICachePort
	getAccUseCase        IGetAccommodationUseCase
	dbTransactionUseCase IDatabaseTransactionUseCase
}

func (u UpdateAccommodationUseCase) AddUnitToAccommodation(ctx context.Context, req *request.AddUnitToAccommodationDto) (*entity.UnitEntity, error) {
	_, err := u.getAccUseCase.GetAccommodationByID(ctx, req.AccommodationID)
	if err != nil {
		log.Error(ctx, "GetAccommodationByID error ", err)
		return nil, err
	}

	tx := u.dbTransactionUseCase.StartTransaction()
	defer func() {
		if errRollBack := u.dbTransactionUseCase.Rollback(tx); errRollBack != nil {
			log.Error(ctx, "Rollback add unit to accommodation failed, : ", errRollBack)
		} else {
			log.Info(ctx, "Rollback add unit to accommodation success")
		}
	}()

	unit := request.ToUnitEntity(req)
	unit, err = u.unitPort.CreateUnit(ctx, tx, unit)
	if err != nil {
		log.Error(ctx, "Create unit failed, : ", err)
		return nil, err
	}

	errCommit := u.dbTransactionUseCase.Commit(tx)
	if errCommit != nil {
		log.Error(ctx, "Commit add unit to accommodation failed, : ", errCommit)
		return nil, errCommit
	}

	// delete from cache
	go func() {
		err := u.cachePort.DeleteFromCache(ctx, utils.BuildCacheKeyGetAccommodation(req.AccommodationID))
		if err != nil {
			log.Error(ctx, "Delete accommodation cache failed, : ", err)
		}
	}()

	return unit, nil
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

	// delete from cache
	go func() {
		err := u.cachePort.DeleteFromCache(ctx, utils.BuildCacheKeyGetAccommodation(accommodation.ID))
		if err != nil {
			log.Error(ctx, "Delete accommodation cache failed, : ", err)
		}
	}()

	return existedAcc, nil
}

func NewUpdateAccommodationUseCase(accPort port.IAccommodationPort, unitPort port.IUnitPort,
	getAccUseCase IGetAccommodationUseCase,
	dbTransactionUseCase IDatabaseTransactionUseCase,
	cachePort port.ICachePort) IUpdateAccommodationUseCase {
	return &UpdateAccommodationUseCase{
		accPort:              accPort,
		unitPort:             unitPort,
		cachePort:            cachePort,
		getAccUseCase:        getAccUseCase,
		dbTransactionUseCase: dbTransactionUseCase,
	}
}
