package service

import (
	"context"
	"memo_service/src/core/domain/dto/request"
	"memo_service/src/core/domain/entity"
	"memo_service/src/core/usecase"
)

type IMemoService interface {
	CreateMemo(ctx context.Context, userID int64, createMemoDto *request.CreateMemoDto) (*entity.Memo, error)
	GetMemoByID(ctx context.Context, userID, memoID int64) (*entity.Memo, error)
}

type MemoService struct {
	createMemoUseCase usecase.ICreateMemoUsecase
	getMemoUseCase    usecase.IGetMemoUsecase
}

func (m *MemoService) CreateMemo(ctx context.Context, userID int64, createMemoDto *request.CreateMemoDto) (*entity.Memo, error) {
	return m.createMemoUseCase.CreateMemo(ctx, userID, createMemoDto)
}

func (m *MemoService) GetMemoByID(ctx context.Context, userID int64, memoID int64) (*entity.Memo, error) {
	return m.getMemoUseCase.GetMemoByID(ctx, memoID)
}

func NewMemoService(
	createMemoUseCase usecase.ICreateMemoUsecase,
	getMemoUseCase usecase.IGetMemoUsecase,
) IMemoService {
	return &MemoService{
		createMemoUseCase: createMemoUseCase,
		getMemoUseCase:    getMemoUseCase,
	}
}
