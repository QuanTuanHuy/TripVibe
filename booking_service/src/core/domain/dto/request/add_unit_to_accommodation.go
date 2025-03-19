package request

import "booking_service/core/domain/entity"

type AddUnitToAccommodationDto struct {
	AccommodationID int64          `json:"accommodationId"`
	Unit            *CreateUnitDto `json:"unit"`
}

func ToUnitEntity(dto *AddUnitToAccommodationDto) *entity.UnitEntity {
	return &entity.UnitEntity{
		BaseEntity: entity.BaseEntity{
			ID: dto.Unit.ID,
		},
		Name:            dto.Unit.Name,
		AccommodationID: dto.AccommodationID,
	}
}
