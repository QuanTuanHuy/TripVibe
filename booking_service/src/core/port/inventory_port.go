package port

import (
	"booking_service/core/domain/dto/request"
	"booking_service/core/domain/dto/response"
	"context"
)

type IInventoryPort interface {
	AcquireLock(ctx context.Context, req *request.InventoryLockRequest) (*response.InventoryLockResponse, error)

	ReleaseLock(ctx context.Context, lockID string) error

	ConfirmBooking(ctx context.Context, req *request.ConfirmBookingRequest) (*response.ConfirmBookingResponse, error)

	CancelBooking(ctx context.Context, req *request.CancelBookingRequest) (*response.CancelBookingResponse, error)
}
