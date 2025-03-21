package usecase

import (
	"booking_service/core/port"
	"booking_service/kernel/utils"
	"context"
	"github.com/golibs-starter/golib/log"
)

type IDeleteUnitUseCase interface {
	DeleteUnit(ctx context.Context, accID, unitID int64) error
}

type DeleteUnitUseCase struct {
	unitPort             port.IUnitPort
	cachePort            port.ICachePort
	dbTransactionUseCase IDatabaseTransactionUseCase
}

func (d DeleteUnitUseCase) DeleteUnit(ctx context.Context, accID, unitID int64) error {
	_, err := d.unitPort.GetUnitByAccIDAndID(ctx, accID, unitID)
	if err != nil {
		log.Error(ctx, "get unit by id failed ", err)
		return err
	}

	tx := d.dbTransactionUseCase.StartTransaction()
	defer func() {
		if errRollBack := d.dbTransactionUseCase.Rollback(tx); errRollBack != nil {
			log.Error(ctx, "Rollback delete unit failed, : ", errRollBack)
		} else {
			log.Info(ctx, "Rollback delete unit success")
		}
	}()

	err = d.unitPort.DeleteUnitByID(ctx, tx, unitID)
	if err != nil {
		log.Error(ctx, "delete unit failed, : ", err)
		return err
	}

	errCommit := d.dbTransactionUseCase.Commit(tx)
	if errCommit != nil {
		log.Error(ctx, "Commit delete unit failed, : ", errCommit)
		return errCommit
	}

	// clear cache
	go func() {
		err := d.cachePort.DeleteFromCache(ctx, utils.BuildCacheKeyGetAccommodation(accID))
		if err != nil {
			log.Error(ctx, "delete unit from cache failed", err)
		}
	}()

	return nil
}

func NewDeleteUnitUseCase(unitPort port.IUnitPort, dbTransactionUseCase IDatabaseTransactionUseCase,
	cachePort port.ICachePort) IDeleteUnitUseCase {
	return &DeleteUnitUseCase{
		unitPort:             unitPort,
		cachePort:            cachePort,
		dbTransactionUseCase: dbTransactionUseCase,
	}
}
