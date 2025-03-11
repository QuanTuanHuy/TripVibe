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

type UserAdapter struct {
	base
}

func (u UserAdapter) UpdateUserByID(ctx context.Context, tx *gorm.DB, user *entity.UserEntity) (*entity.UserEntity, error) {
	userModel := mapper.ToUserModel(user)
	if err := tx.WithContext(ctx).Model(&model.UserModel{}).
		Where("id = ?", user.ID).
		Updates(userModel).Error; err != nil {
		return nil, err
	}
	return mapper.ToUserEntity(userModel), nil
}

func (u UserAdapter) CreateUser(ctx context.Context, tx *gorm.DB, user *entity.UserEntity) (*entity.UserEntity, error) {
	userModel := mapper.ToUserModel(user)
	if err := tx.WithContext(ctx).Create(&userModel).Error; err != nil {
		return nil, err
	}
	return mapper.ToUserEntity(userModel), nil
}

func (u UserAdapter) GetUserByID(ctx context.Context, ID int64) (*entity.UserEntity, error) {
	userModel := &model.UserModel{}
	if err := u.db.WithContext(ctx).Model(&model.UserModel{}).Where("id = ?", ID).First(userModel).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, errors.New(constant.ErrUserNotFound)
		}
		return nil, err
	}
	return mapper.ToUserEntity(userModel), nil
}

func NewUserAdapter(db *gorm.DB) port.IUserPort {
	return &UserAdapter{
		base: base{db: db},
	}
}
