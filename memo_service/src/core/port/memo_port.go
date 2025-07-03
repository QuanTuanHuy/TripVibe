package port

import (
	"context"
	"memo_service/src/core/domain/entity"
)

type IMemoPort interface {
	CreateMemo(ctx context.Context, memo *entity.Memo) (*entity.Memo, error)
	GetMemoByID(ctx context.Context, memoID int64) (*entity.Memo, error)
}
