package client

import (
	"booking_service/core/domain/constant"
	"booking_service/core/domain/dto/request"
	"booking_service/core/domain/dto/response"
	"booking_service/core/port"
	"context"
	"fmt"
	"time"

	"github.com/golibs-starter/golib/log"
)

type InventoryClientAdapter struct {
	apiClient *ApiClient
}

func (i *InventoryClientAdapter) CheckOutBooking(ctx context.Context, req *request.CheckOutInventoryRequest) (*response.CheckOutResponse, error) {
	err := i.SetAuthToken(ctx)
	if err != nil {
		return nil, err
	}

	var result response.CheckOutResponse
	err = i.apiClient.PostJSON(ctx, constant.INVENTORY_SERVICE, constant.CHECK_OUT_BOOKING, req, &result)
	if err != nil {
		log.Error(ctx, "error calling inventory service to check out booking, ", err)
		return nil, err
	}
	return &result, nil
}

func (i *InventoryClientAdapter) CheckInBooking(ctx context.Context, req *request.CheckInInventoryRequest) (*response.CheckInResponse, error) {
	err := i.SetAuthToken(ctx)
	if err != nil {
		return nil, err
	}

	var result response.CheckInResponse
	err = i.apiClient.PostJSON(ctx, constant.INVENTORY_SERVICE, constant.CHECK_IN_BOOKING, req, &result)
	if err != nil {
		log.Error(ctx, "error calling inventory service to check in booking, ", err)
		return nil, err
	}
	return &result, nil
}

func (i *InventoryClientAdapter) CancelBooking(ctx context.Context, req *request.CancelBookingRequest) (*response.CancelBookingResponse, error) {
	var result response.CancelBookingResponse

	err := i.SetAuthToken(ctx)
	if err != nil {
		return nil, err
	}

	err = i.apiClient.PutJSON(ctx, constant.INVENTORY_SERVICE, constant.CANCEL_BOOKING_INVENTORY, req, &result)
	if err != nil {
		log.Error(ctx, "error calling inventory service to cancel booking, ", err)
		return nil, err
	}
	return &result, nil
}

func (i *InventoryClientAdapter) ReleaseLock(ctx context.Context, lockID string) error {
	err := i.SetAuthToken(ctx)
	if err != nil {
		return err
	}

	var result struct {
		Data bool `json:"data"`
		Meta interface{}
	}
	err = i.apiClient.DeleteJSON(ctx, constant.INVENTORY_SERVICE, fmt.Sprintf(constant.RELEASE_LOCK, lockID), &result)
	if err != nil {
		log.Error(ctx, "error calling inventory service to release lock, ", err)
		return err
	}
	if !result.Data {
		log.Error(ctx, "error releasing lock for booking, lockID: ", lockID)
		return fmt.Errorf("error releasing lock for booking, lockID: %s", lockID)
	}
	return nil
}

func (i *InventoryClientAdapter) ConfirmBooking(ctx context.Context, req *request.ConfirmBookingRequest) (*response.ConfirmBookingResponse, error) {
	err := i.SetAuthToken(ctx)
	if err != nil {
		return nil, err
	}

	var result response.ConfirmBookingResponse
	err = i.apiClient.PutJSON(ctx, constant.INVENTORY_SERVICE, constant.CONFIRM_BOOKING, req, &result)
	if err != nil {
		log.Error(ctx, "error calling inventory service to confirm booking, ", err)
		return nil, err
	}
	return &result, nil
}

func (i *InventoryClientAdapter) AcquireLock(ctx context.Context, req *request.InventoryLockRequest) (*response.InventoryLockResponse, error) {
	err := i.SetAuthToken(ctx)
	if err != nil {
		return nil, err
	}

	var result response.InventoryLockResponse
	err = i.apiClient.PostJSON(ctx, constant.INVENTORY_SERVICE, constant.LOCK_ROOM_FOR_BOOKING, req, &result)
	if err != nil {
		log.Error(ctx, "error calling inventory service", err)
		return nil, err
	}
	return &result, nil
}

func (i *InventoryClientAdapter) SetAuthToken(ctx context.Context) error {
	token, ok := ctx.Value("token").(string)
	if !ok {
		log.Error(ctx, "error getting token from context")
		return fmt.Errorf("error getting token from context")
	}

	err := i.apiClient.SetAuthToken(constant.INVENTORY_SERVICE, token)
	if err != nil {
		log.Error(ctx, "error setting auth token for inventory service, ", err)
		return err
	}
	return nil
}

func NewInventoryClientAdapter() port.IInventoryPort {
	apiClient := NewApiClient(
		WithService(constant.INVENTORY_SERVICE, constant.INVENTORY_SERVICE_URL, 10*time.Second),
		WithServiceRetry(constant.INVENTORY_SERVICE, 3, 500*time.Millisecond),
	)
	return &InventoryClientAdapter{
		apiClient: apiClient,
	}
}
