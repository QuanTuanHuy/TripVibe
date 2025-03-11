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

type IGetUserUseCase interface {
	GetUserByID(ctx context.Context, ID int64) (*entity.UserEntity, error)
}

type GetUserUseCase struct {
	userPort  port.IUserPort
	cachePort port.ICachePort
}

func (g GetUserUseCase) GetUserByID(ctx context.Context, ID int64) (*entity.UserEntity, error) {
	// get from cache
	cachedUser, err := g.cachePort.GetFromCache(ctx, utils.BuildCacheKeyGetUser(ID))
	if err != nil && err.Error() != constant.ErrCacheKeyNil {
		log.Error(ctx, "Get User failed", err)
		return nil, err
	}
	if cachedUser != nil {
		response := entity.UserEntity{}
		err = json.Unmarshal([]byte(cachedUser.(string)), &response)
		if err != nil {
			log.Error(ctx, "Unmarshal error", err)
			return nil, err
		}
		return &response, nil
	}

	user, err := g.userPort.GetUserByID(ctx, ID)
	if err != nil {
		log.Error(ctx, "get user by id failed ", err)
		return nil, err
	}

	// set to cache
	go func() {
		err = g.cachePort.SetToCache(ctx, utils.BuildCacheKeyGetUser(ID), user, constant.DefaultCacheTTL)
		if err != nil {
			log.Error(ctx, "Set User to cache failed", err)
		}
	}()

	return user, nil
}

func NewGetUserUseCase(userPort port.IUserPort, cachePort port.ICachePort) IGetUserUseCase {
	return &GetUserUseCase{
		userPort:  userPort,
		cachePort: cachePort,
	}
}
