package mapper

import (
	"booking_service/core/domain/dto/request"
	"booking_service/core/domain/entity"
)

func ToUserEntityUpdate(existedUser *entity.UserEntity, tourist *request.Tourist) {
	existedUser.Email = tourist.Email
	existedUser.LastName = tourist.LastName
	existedUser.FirstName = tourist.FirstName
}

func ToUserEntity(tourist *request.Tourist) *entity.UserEntity {
	return &entity.UserEntity{
		BaseEntity: entity.BaseEntity{
			ID: tourist.TouristID,
		},
		Email:     tourist.Email,
		LastName:  tourist.LastName,
		FirstName: tourist.FirstName,
	}
}
