package service

import (
	"booking_service/core/port"
	"context"
	"fmt"
	"github.com/redis/go-redis/v9"
	"strconv"
	"time"
)

type InventoryAdapter struct {
	redisClient    *redis.Client
	lockExpiration time.Duration
}

// inventoryKey generates a Redis key for an inventory item
func inventoryKey(accommodationID int64, unitID int64, date int) string {
	return fmt.Sprintf("inventory:%d:%d:%d", accommodationID, unitID, date)
}

// lockKey generates a Redis key for a temporary lock
func lockKey(lockID string) string {
	return fmt.Sprintf("lock:%s", lockID)
}

func (i *InventoryAdapter) LockInventory(ctx context.Context, accommodationID int64, unitIDs []int64, startDate, endDate int) (bool, error) {
	// Generate all dates between start and end (inclusive)
	var dates []int
	for d := startDate; d <= endDate; d += 1 {
		dates = append(dates, d)
	}

	// Create a unique lock ID
	lockID := fmt.Sprintf("%d:%v:%d:%d", accommodationID, unitIDs, startDate, endDate)

	// Check if any unit is already booked for any of the dates
	pipe := i.redisClient.Pipeline()
	checkCmds := make([]*redis.IntCmd, 0, len(unitIDs)*len(dates))

	for _, unitID := range unitIDs {
		for _, date := range dates {
			key := inventoryKey(accommodationID, unitID, date)
			checkCmds = append(checkCmds, pipe.Exists(ctx, key))
		}
	}

	_, err := pipe.Exec(ctx)
	if err != nil {
		return false, err
	}

	// If any unit is already booked for any date, return false
	for _, cmd := range checkCmds {
		if cmd.Val() > 0 {
			return false, nil
		}
	}

	// All units are available, lock them
	pipe = i.redisClient.Pipeline()

	// Set the main lock reference
	pipe.Set(ctx, lockKey(lockID), "locked", i.lockExpiration)

	// Lock individual inventory items
	for _, unitID := range unitIDs {
		for _, date := range dates {
			key := inventoryKey(accommodationID, unitID, date)
			pipe.Set(ctx, key, lockID, i.lockExpiration)
		}
	}

	_, err = pipe.Exec(ctx)
	if err != nil {
		return false, err
	}

	return true, nil
}

func (i *InventoryAdapter) ReleaseLock(ctx context.Context, lockID string) error {
	// Parse lockID to extract accommodation ID, unit IDs, and date range
	var accommodationID int64
	var unitIDs []int64
	var startDate, endDate int

	_, err := fmt.Sscanf(lockID, "lock:%d:%v:%d:%d", &accommodationID, &unitIDs, &startDate, &endDate)
	if err != nil {
		return fmt.Errorf("failed to parse lock ID: %w", err)
	}

	// Generate all keys that need to be released
	keys := []string{fmt.Sprintf("lock:%s", lockID)}

	// Generate all dates between start and end (inclusive)
	for d := startDate; d <= endDate; d += 1 {
		for _, unitID := range unitIDs {
			key := inventoryKey(accommodationID, unitID, d)
			keys = append(keys, key)
		}
	}

	if len(keys) > 0 {
		return i.redisClient.Del(ctx, keys...).Err()
	}

	return nil
}

func (i *InventoryAdapter) ConfirmBooking(ctx context.Context, bookingID int64, accommodationID int64, unitIDs []int64, startDate, endDate int) error {
	// Generate all dates between start and end (inclusive)
	var dates []int
	for d := startDate; d <= endDate; d += 1 { // Add seconds in a day
		dates = append(dates, d)
	}

	pipe := i.redisClient.Pipeline()

	// Confirm booking by setting permanent inventory records with booking ID
	for _, unitID := range unitIDs {
		for _, date := range dates {
			key := inventoryKey(accommodationID, unitID, date)
			// Update with booking ID and no expiration
			pipe.Set(ctx, key, strconv.FormatInt(bookingID, 10), 0)
		}
	}

	_, err := pipe.Exec(ctx)
	return err
}

func NewInventoryAdapter(redisClient *redis.Client) port.IInventoryPort {
	return &InventoryAdapter{
		redisClient:    redisClient,
		lockExpiration: 15 * time.Minute, // Lock expires after 15 minutes if not confirmed
	}
}
