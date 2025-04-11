package port

import "context"

type IInventoryPort interface {
	LockInventory(ctx context.Context, accID int64, unitIDs []int64, startDate, endDate int) (bool, error)
	ReleaseLock(ctx context.Context, lockID string) error
	ConfirmBooking(ctx context.Context, bookingID int64, accommodationID int64, unitIDs []int64, startDate, endDate int) error
}
