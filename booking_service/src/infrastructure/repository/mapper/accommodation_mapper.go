package mapper

import (
	"booking_service/core/domain/entity"
	"booking_service/infrastructure/repository/model"
)

func ToAccommodationEntity(accommodation *model.AccommodationModel) *entity.AccommodationEntity {
	return &entity.AccommodationEntity{
		BaseEntity: entity.BaseEntity{
			ID:        accommodation.ID,
			CreatedAt: accommodation.CreatedAt.Unix(),
			UpdatedAt: accommodation.UpdatedAt.Unix(),
		},
		Name: accommodation.Name,
	}
}

func ToAccommodationModel(accommodation *entity.AccommodationEntity) *model.AccommodationModel {
	return &model.AccommodationModel{
		BaseModel: model.BaseModel{
			ID: accommodation.ID,
		},
		Name: accommodation.Name,
	}
}
