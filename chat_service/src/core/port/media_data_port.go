package port

import (
	"chat_service/core/domain/entity"
	"context"

	"gorm.io/gorm"
)

type IMediaDataPort interface {
	CreateMedia(ctx context.Context, tx *gorm.DB, mediaData *entity.MediaDataEntity) (*entity.MediaDataEntity, error)
	GetMediaDataByIDs(ctx context.Context, IDs []int64) ([]*entity.MediaDataEntity, error)
}
