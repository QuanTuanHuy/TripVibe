package usecase

import (
	"context"
	"encoding/json"
	"github.com/golibs-starter/golib/log"
	"notification_service/core/domain/constant"
	"notification_service/core/domain/dto/response"
	"notification_service/core/port"
	"notification_service/kernel/utils"
)

type IGetUserUseCase interface {
	GetUserProfileByID(ctx context.Context, userID int64) (*response.UserProfileDto, error)
}

type GetUserUseCase struct {
	userPort  port.IUserPort
	cachePort port.ICachePort
}

func (g GetUserUseCase) GetUserProfileByID(ctx context.Context, userID int64) (*response.UserProfileDto, error) {
	key := utils.BuildCacheKeyGetUserProfileByID(userID)
	cachedUserProfile, err := g.cachePort.GetFromCache(ctx, key)
	if err != nil && err.Error() != constant.ErrCacheKeyNil {
		return nil, err
	}
	if cachedUserProfile != nil {
		var userProfile response.UserProfileDto
		if err = json.Unmarshal([]byte(cachedUserProfile.(string)), &userProfile); err != nil {
			log.Error(ctx, "unmarshal user profile from cache failed", err)
			return nil, err
		}
		return &userProfile, nil
	}

	userProfile, err := g.userPort.GetUserProfileByID(ctx, userID)
	if err != nil {
		log.Error(ctx, "get user profile failed ", err)
		return nil, err
	}

	go func() {
		err = g.cachePort.SetToCache(ctx, key, userProfile, constant.DefaultTtl)
		if err != nil {
			log.Error(ctx, "set user profile to cache failed", err)
		}
	}()

	return userProfile, nil
}

func NewGetUserUseCase(userPort port.IUserPort, cachePort port.ICachePort) IGetUserUseCase {
	return &GetUserUseCase{
		userPort:  userPort,
		cachePort: cachePort,
	}
}
