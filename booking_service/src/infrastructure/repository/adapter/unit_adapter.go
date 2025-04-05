package adapter

import (
	"booking_service/core/domain/constant"
	"booking_service/core/domain/entity"
	"booking_service/core/port"
	"booking_service/infrastructure/repository/mapper"
	"booking_service/infrastructure/repository/model"
	"context"
	"errors"
	"gorm.io/gorm"
)

type UnitAdapter struct {
	base
}

func (u UnitAdapter) DeleteUnitByID(ctx context.Context, tx *gorm.DB, unitID int64) error {
	if err := tx.WithContext(ctx).Where("id = ?", unitID).
		Delete(&model.UnitModel{}).Error; err != nil {
		return err
	}
	return nil
}

func (u UnitAdapter) GetUnitByAccIDAndID(ctx context.Context, accID, unitID int64) (*entity.UnitEntity, error) {
	unitModel := &model.UnitModel{}
	if err := u.db.WithContext(ctx).Where("accommodation_id = ? AND id = ?", accID, unitID).
		First(unitModel).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, errors.New(constant.ErrUnitNotFound)
		}
		return nil, err
	}
	return mapper.ToUnitEntity(unitModel), nil
}

func (u UnitAdapter) CreateUnit(ctx context.Context, tx *gorm.DB, unit *entity.UnitEntity) (*entity.UnitEntity, error) {
	unitModel := mapper.ToUnitModel(unit)
	if err := tx.WithContext(ctx).Create(unitModel).Error; err != nil {
		return nil, err
	}
	return mapper.ToUnitEntity(unitModel), nil
}

func (u UnitAdapter) DeleteUnitsByAccID(ctx context.Context, tx *gorm.DB, accID int64) error {
	if err := tx.WithContext(ctx).Where("accommodation_id = ?", accID).Delete(&model.UnitModel{}).Error; err != nil {
		return err
	}
	return nil
}

func (u UnitAdapter) GetUnitsByAccID(ctx context.Context, accID int64) ([]*entity.UnitEntity, error) {
	unitModels := []*model.UnitModel{}
	if err := u.db.WithContext(ctx).Where("accommodation_id = ?", accID).Find(&unitModels).Error; err != nil {
		return nil, err
	}
	return mapper.ToListUnitEntity(unitModels), nil
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
