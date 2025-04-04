package client

import (
	"context"
	"github.com/golibs-starter/golib/log"
	"notification_service/core/domain/dto/response"
	"notification_service/core/port"
	"strconv"
	"time"
)

type UserClientAdapter struct {
	apiClient *ApiClient
}

func (u UserClientAdapter) GetUserProfileByID(ctx context.Context, userID int64) (*response.UserProfileDto, error) {
	url := "/api/public/v1/profiles/" + strconv.FormatInt(userID, 10)
	var res response.UserProfileDto
	err := u.apiClient.GetJSON(ctx, "profile", url, &res)
	if err != nil {
		log.Error(ctx, "get user profile failed", err)
		return nil, err
	}
	return &res, nil
}

func NewUserClientAdapter() port.IUserPort {
	apiClient := NewApiClient(
		WithService("profile", "http://localhost:8086/profile_service", 10*time.Second),
		WithServiceRetry("profile", 3, 500*time.Millisecond),
	)
	return &UserClientAdapter{
		apiClient: apiClient,
	}
}
