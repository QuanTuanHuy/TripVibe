package request

import "booking_service/core/domain/entity"

type UpdateAccommodationDto struct {
	ID    int64           `json:"id"`
	Name  string          `json:"name"`
	Units []UpdateUnitDto `json:"units"`
}

type UpdateUnitDto struct {
	ID   int64  `json:"id"`
	Name string `json:"name"`
}

func FromUpdateDtoToAccommodationEntity(dto UpdateAccommodationDto) *entity.AccommodationEntity {
	acc := &entity.AccommodationEntity{
		BaseEntity: entity.BaseEntity{
			ID: dto.ID,
		},
		Name: dto.Name,
	}

	for _, unit := range dto.Units {
		acc.Units = append(acc.Units, &entity.UnitEntity{
			BaseEntity: entity.BaseEntity{
				ID: unit.ID,
			},
			Name:            unit.Name,
			AccommodationID: dto.ID,
		})
	}

	return acc
}
