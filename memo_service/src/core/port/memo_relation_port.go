package port

import (
	"context"
	"memo_service/src/core/domain/entity"
)

type IMemoRelationPort interface {
	CreateMemoRelation(ctx context.Context, memoRelation *entity.MemoRelation) (*entity.MemoRelation, error)
	GetMemoRelationByMemoID(ctx context.Context, memoID int64) ([]*entity.MemoRelation, error)
}
