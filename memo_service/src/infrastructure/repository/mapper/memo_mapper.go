package mapper

import (
	"memo_service/src/core/domain/entity"
	"memo_service/src/infrastructure/repository/model"
)

func ToMemoEntity(memo *model.MemoModel) *entity.Memo {
	return &entity.Memo{
		BaseEntity: entity.BaseEntity{
			ID:        memo.ID,
			CreatedAt: memo.CreatedAt.Unix(),
			UpdatedAt: memo.UpdatedAt.Unix(),
		},
		CreatorID:  memo.CreatorID,
		Content:    memo.Content,
		Visibility: entity.Visibility(memo.Visibility),
		RowStatus:  entity.RowStatus(memo.RowStatus),
		Pinned:     memo.Pinned,
		ParentID:   memo.ParentID,
	}
}

func ToMemoModel(memo *entity.Memo) *model.MemoModel {
	return &model.MemoModel{
		BaseModel: model.BaseModel{
			ID: memo.ID,
		},
		CreatorID:  memo.CreatorID,
		Content:    memo.Content,
		Visibility: string(memo.Visibility),
		RowStatus:  string(memo.RowStatus),
		Pinned:     memo.Pinned,
		ParentID:   memo.ParentID,
	}
}

func ToListMemoEntity(memos []*model.MemoModel) []*entity.Memo {
	var memoEntities []*entity.Memo
	for _, memo := range memos {
		memoEntities = append(memoEntities, ToMemoEntity(memo))
	}
	return memoEntities
}
