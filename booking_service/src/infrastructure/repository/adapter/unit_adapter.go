package adapter

import (
	"booking_service/core/domain/entity"
	"booking_service/core/port"
	"booking_service/infrastructure/repository/mapper"
	"context"
	"gorm.io/gorm"
)

type UnitAdapter struct {
	base
}

func (u UnitAdapter) CreateUnits(ctx context.Context, tx *gorm.DB, units []*entity.UnitEntity) ([]*entity.UnitEntity, error) {
	unitModels := mapper.ToListUnitModel(units)
	if err := tx.WithContext(ctx).Model(&unitModels).Create(unitModels).Error; err != nil {
		return nil, err
	}
	return mapper.ToListUnitEntity(unitModels), nil
}

func NewUnitAdapter(db *gorm.DB) port.IUnitPort {
	return &UnitAdapter{
		base: base{db: db},
	}
}
