package mapper

import (
	"memo_service/src/core/domain/entity"
	"memo_service/src/infrastructure/repository/model"
)

func ToUserEntity(user *model.UserModel) *entity.User {
	return &entity.User{
		BaseEntity: entity.BaseEntity{
			ID:        user.ID,
			CreatedAt: user.CreatedAt.Unix(),
			UpdatedAt: user.UpdatedAt.Unix(),
		},
		Username:  user.Username,
		Email:     user.Email,
		AvatarURL: user.Avatar,
	}
}

func ToUserModel(user *entity.User) *model.UserModel {
	return &model.UserModel{
		BaseModel: model.BaseModel{
			ID: user.ID,
		},
		Username: user.Username,
		Email:    user.Email,
		Avatar:   user.AvatarURL,
	}
}

func ToListUserEntity(users []*model.UserModel) []*entity.User {
	var userEntities []*entity.User
	for _, user := range users {
		userEntities = append(userEntities, ToUserEntity(user))
	}
	return userEntities
}
