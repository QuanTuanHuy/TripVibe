package adapter

import (
	"context"
	"errors"
	"memo_service/src/core/domain/entity"
	"memo_service/src/core/port"
	"memo_service/src/infrastructure/repository/mapper"
	"memo_service/src/infrastructure/repository/model"

	"gorm.io/gorm"
)

type UserAdapter struct {
	base
}

func (u UserAdapter) CreateUser(ctx context.Context, user *entity.User) (*entity.User, error) {
	userModel := mapper.ToUserModel(user)
	if err := u.db.WithContext(ctx).Create(&userModel).Error; err != nil {
		return nil, err
	}
	return mapper.ToUserEntity(userModel), nil
}

func (u UserAdapter) GetUserByID(ctx context.Context, id int64) (*entity.User, error) {
	userModel := &model.UserModel{}
	if err := u.db.WithContext(ctx).Where("id = ?", id).First(&userModel).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, errors.New("UserNotFound")
		}
		return nil, err
	}
	return mapper.ToUserEntity(userModel), nil
}

func (u UserAdapter) UpdateUser(ctx context.Context, tx *gorm.DB, user *entity.User) (*entity.User, error) {
	userModel := mapper.ToUserModel(user)
	if err := tx.WithContext(ctx).Where("id = ?", userModel.ID).Updates(userModel).Error; err != nil {
		return nil, err
	}
	return mapper.ToUserEntity(userModel), nil
}

func (u UserAdapter) GetUserByEmail(ctx context.Context, email string) (*entity.User, error) {
	userModel := &model.UserModel{}
	if err := u.db.WithContext(ctx).Where("email = ?", email).First(&userModel).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, errors.New("UserNotFound")
		}
		return nil, err
	}
	return mapper.ToUserEntity(userModel), nil
}

func (u UserAdapter) GetUserByUsername(ctx context.Context, username string) (*entity.User, error) {
	userModel := &model.UserModel{}
	if err := u.db.WithContext(ctx).Where("username = ?", username).First(&userModel).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, errors.New("UserNotFound")
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
