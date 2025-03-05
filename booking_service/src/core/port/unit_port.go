package port

import (
	"booking_service/core/domain/entity"
	"context"
	"gorm.io/gorm"
)

type IUnitPort interface {
	CreateUnits(ctx context.Context, tx *gorm.DB, units []*entity.UnitEntity) ([]*entity.UnitEntity, error)
}
