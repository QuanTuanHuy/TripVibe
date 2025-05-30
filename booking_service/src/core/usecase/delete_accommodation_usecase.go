package usecase

import (
	"booking_service/core/port"
	"booking_service/kernel/utils"
	"context"
	"github.com/golibs-starter/golib/log"
)

type IDeleteAccommodationUseCase interface {
	DeleteAccommodationByID(ctx context.Context, ID int64) error
}

type DeleteAccommodationUseCase struct {
	accPort              port.IAccommodationPort
	unitPort             port.IUnitPort
	getAccUseCase        IGetAccommodationUseCase
	dbTransactionUseCase IDatabaseTransactionUseCase
	cachePort            port.ICachePort
}

func (d DeleteAccommodationUseCase) DeleteAccommodationByID(ctx context.Context, ID int64) error {
	_, err := d.getAccUseCase.GetAccommodationByID(ctx, ID)
	if err != nil {
		log.Error(ctx, "get accommodation by id failed", err)
		return err
	}

	tx := d.dbTransactionUseCase.StartTransaction()
	defer func() {
		if errRollBack := d.dbTransactionUseCase.Rollback(tx); errRollBack != nil {
			log.Error(ctx, "Rollback delete accommodation failed, : ", errRollBack)
		} else {
			log.Info(ctx, "Rollback delete accommodation success")
		}
	}()

	err = d.unitPort.DeleteUnitsByAccID(ctx, tx, ID)
	if err != nil {
		log.Error(ctx, "delete units failed", err)
		return err
	}

	err = d.accPort.DeleteAccommodation(ctx, tx, ID)
	if err != nil {
		log.Error(ctx, "delete accommodation failed", err)
		return err
	}

	errCommit := d.dbTransactionUseCase.Commit(tx)
	if errCommit != nil {
		log.Error(ctx, "Commit delete accommodation failed, : ", errCommit)
		return errCommit
	}

	// delete from cache
	go func() {
		err = d.cachePort.DeleteFromCache(ctx, utils.BuildCacheKeyGetAccommodation(ID))
		if err != nil {
			log.Error(ctx, "delete accommodation from cache failed", err)
		}
	}()

	return nil
}

func NewDeleteAccommodationUseCase(accPort port.IAccommodationPort, unitPort port.IUnitPort,
	dbTransactionUseCase IDatabaseTransactionUseCase,
	getAccUseCase IGetAccommodationUseCase, cachePort port.ICachePort) IDeleteAccommodationUseCase {
	return &DeleteAccommodationUseCase{accPort: accPort, unitPort: unitPort, dbTransactionUseCase: dbTransactionUseCase,
		getAccUseCase: getAccUseCase, cachePort: cachePort}
}
