package client

import (
	"booking_service/core/domain/constant"
	"booking_service/core/domain/dto/request"
	"booking_service/core/port"
	"context"
	"fmt"
	"time"

	"github.com/golibs-starter/golib/web/log"
)

type ChatRoomClientAdapter struct {
	apiClient *ApiClient
}

func (c *ChatRoomClientAdapter) CreateChatRoom(ctx context.Context, req *request.CreateChatRoomDto) (any, error) {
	c.SetAuthToken(ctx)

	var result any
	err := c.apiClient.PostJSON(ctx, constant.CHAT_SERVICE, constant.CREATE_CHAT_ROOM, req, &result)
	if err != nil {
		log.Error(ctx, "error calling chat service to create chat room, ", err)
		return nil, fmt.Errorf("error calling chat service to create chat room: %w", err)
	}

	return result, nil
}

func (c *ChatRoomClientAdapter) SetAuthToken(ctx context.Context) error {
	token, ok := ctx.Value("token").(string)
	if !ok {
		log.Error(ctx, "error getting token from context")
		return fmt.Errorf("error getting token from context")
	}

	err := c.apiClient.SetAuthToken(constant.CHAT_SERVICE, token)
	if err != nil {
		log.Error(ctx, "error setting auth token for inventory service, ", err)
		return err
	}
	return nil
}

func NewChatRoomClientAdapter() port.IChatRoomPort {
	apiClient := NewApiClient(
		WithService(constant.CHAT_SERVICE, constant.CHAT_SERVICE_URL, 10*time.Second),
		WithServiceRetry(constant.CHAT_SERVICE, 3, 500*time.Millisecond),
	)
	return &ChatRoomClientAdapter{
		apiClient: apiClient,
	}
}
