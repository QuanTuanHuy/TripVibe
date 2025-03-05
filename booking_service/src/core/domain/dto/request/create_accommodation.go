package request

import "booking_service/core/domain/entity"

type CreateAccommodationDto struct {
	ID    int64            `json:"id"`
	Name  string           `json:"name"`
	Units []*CreateUnitDto `json:"units"`
}

type CreateUnitDto struct {
	ID   int64  `json:"id"`
	Name string `json:"name"`
}

func ToAccommodationEntity(dto *CreateAccommodationDto) *entity.AccommodationEntity {
	accommodation := &entity.AccommodationEntity{
		BaseEntity: entity.BaseEntity{
			ID: dto.ID,
		},
		Name: dto.Name,
	}

	for _, unitDto := range dto.Units {
		unit := &entity.UnitEntity{
			BaseEntity: entity.BaseEntity{
				ID: unitDto.ID,
			},
			Name:            unitDto.Name,
			AccommodationID: accommodation.ID,
		}
		accommodation.Units = append(accommodation.Units, unit)
	}

	return accommodation
}
