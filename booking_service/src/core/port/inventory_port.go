package port

import (
	"context"

	"gorm.io/gorm"
)

type IInventoryPort interface {
	// LockInventory tạm thời khóa inventory để đặt phòng
	LockInventory(ctx context.Context, unitQuantities map[int64]int, startDate, endDate int) (string, error)

	// ReleaseLock giải phóng khóa tạm thời
	ReleaseLock(ctx context.Context, unitQuantities map[int64]int, startDate, endDate int) error

	// ConfirmBooking xác nhận đặt phòng và cập nhật inventory
	ConfirmBooking(ctx context.Context, tx *gorm.DB, bookingID int64, accommodationID int64,
		unitQuantities map[int64]int, startDate, endDate int) error

	// ReleaseBooking giải phóng phòng khi hủy đặt phòng
	ReleaseBooking(ctx context.Context, tx *gorm.DB, bookingID int64) error

	// CheckAvailability kiểm tra tính khả dụng của các phòng trong khoảng thời gian
	CheckAvailability(ctx context.Context, accommodationID int64, unitQuantities map[int64]int,
		startDate, endDate int) (map[int64]int, error)

	// BlockInventory chặn các phòng trong khoảng thời gian
	BlockInventory(ctx context.Context, tx *gorm.DB, accommodationID int64,
		unitQuantities map[int64]int, startDate, endDate int, note string) error

	// UnblockInventory mở chặn các phòng trong khoảng thời gian
	UnblockInventory(ctx context.Context, tx *gorm.DB, accommodationID int64,
		unitQuantities map[int64]int, startDate, endDate int) error
}
