package adapter

import (
	"chat_service/core/domain/entity"
	"chat_service/core/port"
	"chat_service/infrastructure/repository/mapper"
	"chat_service/infrastructure/repository/model"
	"context"

	"github.com/golibs-starter/golib/log"
	"gorm.io/gorm"
)

type MediaDataAdapter struct {
	base
}

func (m MediaDataAdapter) CreateMedia(ctx context.Context, tx *gorm.DB, mediaData *entity.MediaDataEntity) (*entity.MediaDataEntity, error) {
	mediaDataModel := mapper.ToMediaDataModel(mediaData)
	if err := tx.WithContext(ctx).Create(mediaDataModel).Error; err != nil {
		log.Error(ctx, "Error creating media data: ", err)
		return nil, err
	}
	return mapper.ToMediaDataEntity(mediaDataModel), nil
}

func (m MediaDataAdapter) GetMediaDataByIDs(ctx context.Context, IDs []int64) ([]*entity.MediaDataEntity, error) {
	var mediaDataModels []*model.MediaDataModel
	if err := m.db.WithContext(ctx).
		Where("id IN (?)", IDs).
		Find(&mediaDataModels).Error; err != nil {
		log.Error(ctx, "Error getting media data by IDs: ", err)
		return nil, err
	}
	return mapper.ToMediaDataEntities(mediaDataModels), nil
}

func NewMediaDataAdapter(db *gorm.DB) port.IMediaDataPort {
	return &MediaDataAdapter{
		base: base{
			db: db,
		},
	}
}
