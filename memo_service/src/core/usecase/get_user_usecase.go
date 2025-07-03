package usecase

import (
	"context"
	"memo_service/src/core/domain/entity"
	"memo_service/src/core/port"

	"go.uber.org/zap"
)

type IGetUserUseCase interface {
	GetUserByID(ctx context.Context, ID int64) (*entity.User, error)
}

type GetUserUseCase struct {
	userPort port.IUserPort
	logger   *zap.Logger
}

func (g *GetUserUseCase) GetUserByID(ctx context.Context, ID int64) (*entity.User, error) {
	user, err := g.userPort.GetUserByID(ctx, ID)
	if err != nil {
		g.logger.Error("Failed to get user by ID", zap.Int64("ID", ID), zap.Error(err))
		return nil, err
	}
	return user, nil
}

func NewGetUserUseCase(userPort port.IUserPort, logger *zap.Logger) IGetUserUseCase {
	return &GetUserUseCase{
		userPort: userPort,
		logger:   logger,
	}
}
