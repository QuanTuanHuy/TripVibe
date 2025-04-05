package client

import (
	"context"
	"github.com/golibs-starter/golib/log"
	"notification_service/core/domain/dto/response"
	"notification_service/core/port"
	"strconv"
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

func NewUserClientAdapter(apiClient *ApiClient) port.IUserPort {
	return &UserClientAdapter{
		apiClient: apiClient,
	}
}
