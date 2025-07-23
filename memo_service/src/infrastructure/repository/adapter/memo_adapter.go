package adapter

import (
	"context"
	"errors"
	"memo_service/src/core/domain/entity"
	"memo_service/src/core/port"
	"memo_service/src/infrastructure/repository/mapper"
	"memo_service/src/infrastructure/repository/model"

	"gorm.io/gorm"
)

type MemoAdapter struct {
	base
}

func (m MemoAdapter) CreateMemo(ctx context.Context, memo *entity.Memo) (*entity.Memo, error) {
	memoModel := mapper.ToMemoModel(memo)
	if err := m.db.WithContext(ctx).Create(&memoModel).Error; err != nil {
		return nil, err
	}
	return mapper.ToMemoEntity(memoModel), nil
}

func (m MemoAdapter) GetMemoByID(ctx context.Context, memoID int64) (*entity.Memo, error) {
	memoModel := &model.MemoModel{}
	if err := m.db.WithContext(ctx).Where("id = ?", memoID).First(&memoModel).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, errors.New("MemoNotFound")
		}
		return nil, err
	}
	return mapper.ToMemoEntity(memoModel), nil
}

func (m MemoAdapter) UpdateMemo(ctx context.Context, tx *gorm.DB, memo *entity.Memo) (*entity.Memo, error) {
	memoModel := mapper.ToMemoModel(memo)
	if err := tx.WithContext(ctx).Where("id = ?", memoModel.ID).Updates(memoModel).Error; err != nil {
		return nil, err
	}
	return mapper.ToMemoEntity(memoModel), nil
}

func (m MemoAdapter) DeleteMemo(ctx context.Context, tx *gorm.DB, memoID int64) error {
	if err := tx.WithContext(ctx).Where("id = ?", memoID).Delete(&model.MemoModel{}).Error; err != nil {
		return err
	}
	return nil
}

func (m MemoAdapter) GetMemosByCreatorID(ctx context.Context, creatorID int64, limit, offset int) ([]*entity.Memo, error) {
	var memoModels []*model.MemoModel
	query := m.db.WithContext(ctx).Where("creator_id = ? AND row_status = ?", creatorID, string(entity.Normal))

	if limit > 0 {
		query = query.Limit(limit)
	}
	if offset > 0 {
		query = query.Offset(offset)
	}

	if err := query.Order("created_at DESC").Find(&memoModels).Error; err != nil {
		return nil, err
	}

	return mapper.ToListMemoEntity(memoModels), nil
}

func NewMemoAdapter(db *gorm.DB) port.IMemoPort {
	return &MemoAdapter{
		base: base{db: db},
	}
}
