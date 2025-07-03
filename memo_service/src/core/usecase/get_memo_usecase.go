package usecase

import (
	"context"
	"memo_service/src/core/domain/entity"
	"memo_service/src/core/port"

	"go.uber.org/zap"
)

type IGetMemoUsecase interface {
	GetMemoByID(ctx context.Context, memoID int64) (*entity.Memo, error)
}

type GetMemoUsecase struct {
	memoPort port.IMemoPort
	logger   *zap.Logger
}

func (g *GetMemoUsecase) GetMemoByID(ctx context.Context, memoID int64) (*entity.Memo, error) {
	memo, err := g.memoPort.GetMemoByID(ctx, memoID)
	if err != nil {
		g.logger.Error("Failed to get memo by ID", zap.Int64("memoID", memoID), zap.Error(err))
		return nil, err
	}
	return memo, nil
}

func NewGetMemoUsecase(memoPort port.IMemoPort, logger *zap.Logger) IGetMemoUsecase {
	return &GetMemoUsecase{
		memoPort: memoPort,
		logger:   logger,
	}
}
