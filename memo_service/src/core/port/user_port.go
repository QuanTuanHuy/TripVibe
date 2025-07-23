package port

import (
	"context"
	"memo_service/src/core/domain/entity"
)

type IUserPort interface {
	CreateUser(ctx context.Context, user *entity.User) (*entity.User, error)
	GetUserByID(ctx context.Context, id int64) (*entity.User, error)
}
