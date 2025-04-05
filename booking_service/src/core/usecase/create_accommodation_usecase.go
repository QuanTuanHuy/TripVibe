package usecase

import (
	"booking_service/core/domain/constant"
	"booking_service/core/domain/entity"
	"booking_service/core/port"
	"context"
	"errors"
	"github.com/golibs-starter/golib/log"
)

type ICreateAccommodationUseCase interface {
	CreateAccommodation(ctx context.Context, accommodation *entity.AccommodationEntity) (*entity.AccommodationEntity, error)
}

type CreateAccommodationUseCase struct {
	accPort              port.IAccommodationPort
	unitPort             port.IUnitPort
	getAccUseCase        IGetAccommodationUseCase
	dbTransactionUseCase IDatabaseTransactionUseCase
}

func (c CreateAccommodationUseCase) CreateAccommodation(ctx context.Context, accommodation *entity.AccommodationEntity) (*entity.AccommodationEntity, error) {
	// validate acc not existed
	existedAcc, err := c.getAccUseCase.GetAccommodationByID(ctx, accommodation.ID)
	if err != nil && err.Error() != constant.ErrAccommodationNotFound {
		return nil, err
	}
	if existedAcc != nil {
		return nil, errors.New(constant.ErrAccommodationAlreadyExist)
	}

	// create acc
	tx := c.dbTransactionUseCase.StartTransaction()
	defer func() {
		if errRollBack := c.dbTransactionUseCase.Rollback(tx); errRollBack != nil {
			log.Error(ctx, "Rollback create accommodation failed, : ", errRollBack)
		} else {
			log.Info(ctx, "Rollback create notification success")
		}
	}()

	_, err = c.accPort.CreateAccommodation(ctx, tx, accommodation)
	if err != nil {
		log.Error(ctx, "Create accommodation failed, : ", err.Error())
		return nil, err
	}

	_, err = c.unitPort.CreateUnits(ctx, tx, accommodation.Units)
	if err != nil {
		log.Error(ctx, "Create units failed, : ", err)
	}

	errCommit := c.dbTransactionUseCase.Commit(tx)
	if errCommit != nil {
		log.Error(ctx, "Commit create accommodation failed, : ", errCommit)
		return nil, errCommit
	}

	return accommodation, nil
}

func NewCreateAccommodationUseCase(accPort port.IAccommodationPort, unitPort port.IUnitPort,
	dbTransactionUseCase IDatabaseTransactionUseCase,
	getAccUseCase IGetAccommodationUseCase) ICreateAccommodationUseCase {
	return &CreateAccommodationUseCase{
		accPort:              accPort,
		unitPort:             unitPort,
		getAccUseCase:        getAccUseCase,
		dbTransactionUseCase: dbTransactionUseCase,
	}
}
