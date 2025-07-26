package client

import (
	"context"
	"fmt"
	"notification_service/core/domain/constant"
	"notification_service/core/domain/dto/response"
	"notification_service/core/port"

	"github.com/golibs-starter/golib/log"
)

type UserClientAdapter struct {
	apiClient *ApiClient
}

func (u UserClientAdapter) GetUserProfileByID(ctx context.Context, userID int64) (*response.UserProfileDto, error) {
	url := fmt.Sprintf(constant.GET_PROFILE_BY_ID_PATH, userID)
	var res response.UserProfileDto
	err := u.apiClient.GetJSON(ctx, constant.PROFILE_SERVICE, url, &res)
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
