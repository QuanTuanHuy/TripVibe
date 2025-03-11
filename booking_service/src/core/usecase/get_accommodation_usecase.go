package usecase

import (
	"booking_service/core/domain/constant"
	"booking_service/core/domain/entity"
	"booking_service/core/port"
	"booking_service/kernel/utils"
	"context"
	"encoding/json"
	"github.com/golibs-starter/golib/log"
)

type IGetAccommodationUseCase interface {
	GetAccommodationByID(ctx context.Context, ID int64) (*entity.AccommodationEntity, error)
}

type GetAccommodationUseCase struct {
	accPort   port.IAccommodationPort
	unitPort  port.IUnitPort
	cachePort port.ICachePort
}

func (g GetAccommodationUseCase) GetAccommodationByID(ctx context.Context, ID int64) (*entity.AccommodationEntity, error) {
	// get from cache
	cachedAcc, err := g.cachePort.GetFromCache(ctx, utils.BuildCacheKeyGetAccommodation(ID))
	if err != nil && err.Error() != constant.ErrCacheKeyNil {
		log.Error(ctx, "Get AccommodationByID failed", err)
		return nil, err
	}
	if cachedAcc != nil {
		response := entity.AccommodationEntity{}
		err = json.Unmarshal([]byte(cachedAcc.(string)), &response)
		if err != nil {
			log.Error(ctx, "Unmarshal error", err)
			return nil, err
		}
		return &response, nil
	}

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

	go func() {
		err = g.cachePort.SetToCache(ctx, utils.BuildCacheKeyGetAccommodation(acc.ID), acc, constant.DefaultCacheTTL)
		if err != nil {
			log.Error(ctx, "SetToCache error", err)
		}
	}()

	return acc, nil
}

func NewGetAccommodationUseCase(accPort port.IAccommodationPort, unitPort port.IUnitPort, cachePort port.ICachePort) IGetAccommodationUseCase {
	return &GetAccommodationUseCase{accPort: accPort, unitPort: unitPort, cachePort: cachePort}
}
