package usecase

import (
	"booking_service/core/domain/entity"
	"booking_service/core/port"
	"context"
	"github.com/golibs-starter/golib/log"
)

type IGetAccommodationUseCase interface {
	GetAccommodationByID(ctx context.Context, ID int64) (*entity.AccommodationEntity, error)
}

type GetAccommodationUseCase struct {
	accPort  port.IAccommodationPort
	unitPort port.IUnitPort
}

func (g GetAccommodationUseCase) GetAccommodationByID(ctx context.Context, ID int64) (*entity.AccommodationEntity, error) {
	acc, err := g.accPort.GetAccommodationByID(ctx, ID)
	if err != nil {
		log.Error(ctx, "get accommodation by id failed", err)
		return nil, err
	}

	units, err := g.unitPort.GetUnitsByAccID(ctx, acc.ID)
	if err != nil {
		log.Error(ctx, "get units failed", err)
		return nil, err
	}
	acc.Units = units

	return acc, nil
}

func NewGetAccommodationUseCase(accPort port.IAccommodationPort, unitPort port.IUnitPort) IGetAccommodationUseCase {
	return &GetAccommodationUseCase{accPort: accPort, unitPort: unitPort}
}
