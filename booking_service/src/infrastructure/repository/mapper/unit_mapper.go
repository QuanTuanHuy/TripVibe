package mapper

import (
	"booking_service/core/domain/entity"
	"booking_service/infrastructure/repository/model"
)

func ToUnitModel(unit *entity.UnitEntity) *model.UnitModel {
	return &model.UnitModel{
		BaseModel: model.BaseModel{
			ID: unit.ID,
		},
		Name:            unit.Name,
		AccommodationID: unit.AccommodationID,
		Quantity:        unit.Quantity,
	}
}

func ToUnitEntity(unit *model.UnitModel) *entity.UnitEntity {
	return &entity.UnitEntity{
		BaseEntity: entity.BaseEntity{
			ID:        unit.ID,
			CreatedAt: unit.CreatedAt.Unix(),
			UpdatedAt: unit.UpdatedAt.Unix(),
		},
		Name:            unit.Name,
		AccommodationID: unit.AccommodationID,
		Quantity:        unit.Quantity,
	}
}

func ToListUnitModel(units []*entity.UnitEntity) []*model.UnitModel {
	var list []*model.UnitModel
	for _, u := range units {
		list = append(list, ToUnitModel(u))
	}
	return list
}

func ToListUnitEntity(units []*model.UnitModel) []*entity.UnitEntity {
	var list []*entity.UnitEntity
	for _, u := range units {
		list = append(list, ToUnitEntity(u))
	}
	return list
}
