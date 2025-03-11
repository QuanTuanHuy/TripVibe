package port

import (
	"booking_service/core/domain/entity"
	"context"
	"gorm.io/gorm"
)

type IUserPort interface {
	CreateUser(ctx context.Context, tx *gorm.DB, user *entity.UserEntity) (*entity.UserEntity, error)
	GetUserByID(ctx context.Context, ID int64) (*entity.UserEntity, error)
	UpdateUserByID(ctx context.Context, tx *gorm.DB, user *entity.UserEntity) (*entity.UserEntity, error)
}
