package port

import (
	"booking_service/core/domain/entity"
	"context"
	"gorm.io/gorm"
)

type IUnitPort interface {
	CreateUnits(ctx context.Context, tx *gorm.DB, units []*entity.UnitEntity) ([]*entity.UnitEntity, error)
	CreateUnit(ctx context.Context, tx *gorm.DB, unit *entity.UnitEntity) (*entity.UnitEntity, error)
	GetUnitsByAccID(ctx context.Context, accID int64) ([]*entity.UnitEntity, error)
	DeleteUnitsByAccID(ctx context.Context, tx *gorm.DB, accID int64) error
}
