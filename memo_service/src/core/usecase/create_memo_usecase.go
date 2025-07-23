package usecase

import (
	"context"
	"memo_service/src/core/domain/dto/request"
	"memo_service/src/core/domain/entity"
	"memo_service/src/core/port"

	"go.uber.org/zap"
)

type ICreateMemoUsecase interface {
	CreateMemo(ctx context.Context, userID int64, request *request.CreateMemoDto) (*entity.Memo, error)
}

type CreateMemoUsecase struct {
	memoPort          port.IMemoPort
	dbTransactionPort port.IDBTransactionPort
	logger            *zap.Logger
	getUserUseCase    IGetUserUseCase
}

func (c *CreateMemoUsecase) CreateMemo(ctx context.Context, userID int64, request *request.CreateMemoDto) (*entity.Memo, error) {
	user, err := c.getUserUseCase.GetUserByID(ctx, userID)
	if err != nil {
		c.logger.Error("Failed to get user by ID", zap.Int64("userID", userID), zap.Error(err))
		return nil, err
	}

	tx := c.dbTransactionPort.StartTransaction()
	defer func() {
		if r := recover(); r != nil {
			c.logger.Error("Panic occurred in CreateMemoUsecase", zap.Any("recover", r))
			c.dbTransactionPort.RollbackTransaction(tx)
		}
		err := c.dbTransactionPort.RollbackTransaction(tx)
		if err != nil {
			c.logger.Error("Failed to rollback transaction", zap.Error(err))
		}
	}()

	memo := &entity.Memo{
		CreatorID:  user.ID,
		Content:    request.Content,
		Visibility: entity.Visibility(request.Visibility),
	}
	memo, err = c.memoPort.CreateMemo(ctx, memo)
	if err != nil {
		c.logger.Error("Failed to create memo", zap.Int64("userID", userID), zap.Error(err))
		return nil, err
	}

	err = c.dbTransactionPort.CommitTransaction(tx)
	if err != nil {
		c.logger.Error("Failed to commit transaction", zap.Error(err))
		return nil, err
	}

	return memo, nil
}

func NewCreateMemoUsecase(
	memoPort port.IMemoPort,
	dbTransactionPort port.IDBTransactionPort,
	logger *zap.Logger,
	getUserUseCase IGetUserUseCase,
) ICreateMemoUsecase {
	return &CreateMemoUsecase{
		memoPort:          memoPort,
		dbTransactionPort: dbTransactionPort,
		logger:            logger,
		getUserUseCase:    getUserUseCase,
	}
}
