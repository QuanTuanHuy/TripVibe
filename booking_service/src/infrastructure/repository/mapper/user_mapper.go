package mapper

import (
	"booking_service/core/domain/entity"
	"booking_service/infrastructure/repository/model"
)

func ToUserEntity(user *model.UserModel) *entity.UserEntity {
	return &entity.UserEntity{
		BaseEntity: entity.BaseEntity{
			ID:        user.ID,
			CreatedAt: user.CreatedAt.Unix(),
			UpdatedAt: user.UpdatedAt.Unix(),
		},
		LastName:    user.LastName,
		FirstName:   user.FirstName,
		Email:       user.Email,
		PhoneNumber: user.PhoneNumber,
	}
}

func ToUserModel(user *entity.UserEntity) *model.UserModel {
	return &model.UserModel{
		BaseModel: model.BaseModel{
			ID: user.ID,
		},
		LastName:    user.LastName,
		FirstName:   user.FirstName,
		Email:       user.Email,
		PhoneNumber: user.PhoneNumber,
	}
}
