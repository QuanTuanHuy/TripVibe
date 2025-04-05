package port

import (
	"context"
	"notification_service/core/domain/dto/response"
)

type IUserPort interface {
	GetUserProfileByID(ctx context.Context, userID int64) (*response.UserProfileDto, error)
}
