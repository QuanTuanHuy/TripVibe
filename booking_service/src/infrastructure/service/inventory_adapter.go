package service

import (
	"booking_service/core/domain/constant"
	"booking_service/core/port"
	"booking_service/infrastructure/repository/model"
	"context"
	"fmt"
	"strconv"
	"strings"
	"time"

	"github.com/golibs-starter/golib/log"
	"github.com/redis/go-redis/v9"
	"gorm.io/gorm"
)

type InventoryAdapter struct {
	db          *gorm.DB
	redisClient *redis.Client
	lockTTL     time.Duration
}

// Tạo key cho lock tạm thời
func getTempLockKey(unitID int64, date int) string {
	return fmt.Sprintf("temp_lock:%d:%d", unitID, date)
}

func (i InventoryAdapter) LockInventory(ctx context.Context, unitQuantities map[int64]int, startDate, endDate int) (string, error) {
	// Tạo một lockID duy nhất
	lockID := fmt.Sprintf("lock:%d", time.Now().UnixNano())

	// Generate all dates between start and end (inclusive)
	var dates []int
	for d := startDate; d <= endDate; d++ {
		dates = append(dates, d)
	}

	// Kiểm tra và tạo khóa cho mỗi unit và ngày
	for unitID, quantity := range unitQuantities {
		for _, date := range dates {
			// Kiểm tra số lượng còn lại của inventory
			var inventory model.InventoryModel
			err := i.db.WithContext(ctx).
				Where("unit_id = ? AND date = ?", unitID, date).
				First(&inventory).Error

			// Nếu không tìm thấy, tạo bản ghi inventory mới với số lượng mặc định từ bảng unit
			if err == gorm.ErrRecordNotFound {
				var unit struct {
					Quantity int
				}
				if err := i.db.Table("units").
					Select("quantity").
					Where("id = ?", unitID).
					First(&unit).Error; err != nil {
					return "", err
				}

				inventory = model.InventoryModel{
					UnitID:          unitID,
					AccommodationID: 0, // Sẽ được cập nhật sau trong ConfirmBooking
					Date:            date,
					AvailableCount:  unit.Quantity,
					TotalCount:      unit.Quantity,
					Status:          constant.AVAILABLE,
				}

				if err := i.db.Create(&inventory).Error; err != nil {
					return "", err
				}
			} else if err != nil {
				return "", err
			}

			// Kiểm tra số lượng còn đủ không
			if inventory.AvailableCount < quantity {
				// Không đủ số lượng, giải phóng các khóa đã tạo
				i.ReleaseLock(ctx, unitQuantities, startDate, endDate)
				return "", fmt.Errorf("not enough inventory for unit %d on date %d", unitID, date)
			}
		}
	}

	// Sau khi kiểm tra tất cả date và unit, bắt đầu khóa trong Redis
	pipe := i.redisClient.Pipeline()
	lockKeys := make([]string, 0)

	// Đặt tất cả các lệnh SetNX vào pipeline
	for unitID, quantity := range unitQuantities {
		for _, date := range dates {
			// Tạo khóa trong Redis với số lượng cần khóa
			key := getTempLockKey(unitID, date)
			lockKeys = append(lockKeys, key)
			lockValue := fmt.Sprintf("%s:%d", lockID, quantity)
			pipe.SetNX(ctx, key, lockValue, i.lockTTL)
		}
	}

	// Thực hiện các lệnh Redis trong một lần gọi
	cmds, err := pipe.Exec(ctx)
	if err != nil {
		return "", fmt.Errorf("failed to set Redis locks: %w", err)
	}

	// Kiểm tra xem tất cả các lệnh SetNX có thành công không
	for idx, cmd := range cmds {
		if idx >= len(lockKeys) {
			break // Phòng trường hợp số lượng lệnh trả về không khớp
		}

		setNxCmd, ok := cmd.(*redis.BoolCmd)
		if !ok {
			// Xóa tất cả các khóa đã tạo
			i.ReleaseLock(ctx, unitQuantities, startDate, endDate)
			return "", fmt.Errorf("unexpected Redis command result type for key %s", lockKeys[idx])
		}

		// Nếu có một khóa không đặt được (đã bị người khác khóa)
		if !setNxCmd.Val() {
			// Xóa tất cả các khóa đã tạo
			i.ReleaseLock(ctx, unitQuantities, startDate, endDate)
			return "", fmt.Errorf("failed to acquire lock for key %s, possibly already locked", lockKeys[idx])
		}
	}

	return lockID, nil
}

func (i InventoryAdapter) ReleaseLock(ctx context.Context, unitQuantities map[int64]int, startDate, endDate int) error {
	pipe := i.redisClient.Pipeline()
	deleteKeys := make([]string, 0)

	var dates []int
	for d := startDate; d <= endDate; d++ {
		dates = append(dates, d)
	}

	// Thu thập tất cả các khóa cần xóa trước
	for unitID := range unitQuantities {
		for _, date := range dates {
			key := getTempLockKey(unitID, date)
			deleteKeys = append(deleteKeys, key)
		}
	}

	// Kiểm tra nếu không có khóa nào cần xóa
	if len(deleteKeys) == 0 {
		return nil
	}

	// Thêm tất cả lệnh DEL vào pipeline (gom nhóm để giảm số lệnh)
	// Redis UNLINK hiệu quả hơn DEL cho việc xóa nhiều khóa
	if len(deleteKeys) <= 10 {
		// Nếu ít khóa, sử dụng từng lệnh DEL riêng biệt
		for _, key := range deleteKeys {
			pipe.Del(ctx, key)
		}
	} else {
		// Nếu nhiều khóa, sử dụng lệnh DEL với nhiều khóa
		// Phân chia thành các nhóm nhỏ để tránh gửi quá nhiều tham số
		batchSize := 100
		for i := 0; i < len(deleteKeys); i += batchSize {
			end := i + batchSize
			if end > len(deleteKeys) {
				end = len(deleteKeys)
			}
			batch := deleteKeys[i:end]
			pipe.Del(ctx, batch...)
		}
	}

	// Thực thi pipeline
	_, err := pipe.Exec(ctx)
	if err != nil {
		log.Error(ctx, "Error releasing locks", err, map[string]interface{}{
			"keysCount": len(deleteKeys),
		})
		return fmt.Errorf("failed to release locks: %w", err)
	}

	return nil
}

func (i InventoryAdapter) ConfirmBooking(ctx context.Context, tx *gorm.DB, bookingID int64, accommodationID int64,
	unitQuantities map[int64]int, startDate, endDate int) error {

	// Cập nhật inventory và tạo booking_inventories trong một transaction
	for unitID, quantity := range unitQuantities {
		// Tạo booking_inventories
		bookingInventories := make([]*model.BookingInventoryModel, 0)
		for date := startDate; date <= endDate; date++ {
			bookingInventories = append(bookingInventories, &model.BookingInventoryModel{
				BookingID: bookingID,
				UnitID:    unitID,
				Quantity:  quantity,
				Date:      date,
			})

			// Tìm hoặc tạo inventory nếu chưa tồn tại
			var inventory model.InventoryModel
			err := tx.WithContext(ctx).Where("unit_id = ? AND date = ?", unitID, date).First(&inventory).Error

			if err == gorm.ErrRecordNotFound {
				var unit struct {
					Quantity int
				}
				if err := tx.Table("units").Select("quantity").Where("id = ?", unitID).First(&unit).Error; err != nil {
					return err
				}

				inventory = model.InventoryModel{
					UnitID:          unitID,
					AccommodationID: accommodationID,
					Date:            date,
					AvailableCount:  unit.Quantity,
					TotalCount:      unit.Quantity,
					Status:          constant.AVAILABLE,
				}

				if err := tx.Create(&inventory).Error; err != nil {
					return err
				}
			} else if err != nil {
				return err
			}

			// Cập nhật inventory.available_count và status
			if err := tx.Exec(`
				UPDATE inventories 
				SET available_count = available_count - ?,
					status = CASE 
						WHEN (available_count - ?) <= 0 THEN 'fully_booked'
						WHEN (available_count - ?) < total_count THEN 'partially_booked' 
						ELSE 'available'
					END,
					accommodation_id = ?
				WHERE unit_id = ? AND date = ?`,
				quantity, quantity, quantity, accommodationID, unitID, date).Error; err != nil {
				return err
			}
		}

		// Lưu booking_inventories
		if err := tx.CreateInBatches(bookingInventories, 100).Error; err != nil {
			return err
		}
	}

	// Giải phóng khóa Redis (không chờ kết quả)
	go func() {
		if err := i.ReleaseLock(ctx, unitQuantities, startDate, endDate); err != nil {
			log.Error(ctx, "Error releasing lock", err)
		}
	}()

	return nil
}

func (i InventoryAdapter) ReleaseBooking(ctx context.Context, tx *gorm.DB, bookingID int64) error {
	// Lấy thông tin đặt phòng
	var bookingInventories []*model.BookingInventoryModel
	if err := tx.Where("booking_id = ?", bookingID).Find(&bookingInventories).Error; err != nil {
		return err
	}

	// Nếu không có booking_inventories (có thể là do migration), thử cách khác - legacy fallback
	if len(bookingInventories) == 0 {
		// Không còn hỗ trợ phương thức legacy - yêu cầu phải có booking_inventories
		return fmt.Errorf("no booking inventories found for booking id %d", bookingID)
	}

	// Cập nhật inventory cho từng mục
	for _, bi := range bookingInventories {
		// Cập nhật inventory.available_count và status
		if err := tx.Exec(`
			UPDATE inventories 
			SET available_count = available_count + ?,
				status = CASE 
					WHEN (available_count + ?) >= total_count THEN 'available'
					ELSE 'partially_booked'
				END
			WHERE unit_id = ? AND date = ?`,
			bi.Quantity, bi.Quantity, bi.UnitID, bi.Date).Error; err != nil {
			return err
		}
	}

	// Xóa booking_inventories
	return tx.Where("booking_id = ?", bookingID).Delete(&model.BookingInventoryModel{}).Error
}

func (i InventoryAdapter) CheckAvailability(ctx context.Context, accommodationID int64,
	unitQuantities map[int64]int, startDate, endDate int) (map[int64]int, error) {

	result := make(map[int64]int)

	// Kiểm tra từng unit
	for unitID, _ := range unitQuantities {
		// Tìm ngày có sẵn số lượng ít nhất
		var minAvailable struct {
			MinCount int
		}

		query := `
			SELECT COALESCE(MIN(available_count), 0) as min_count 
			FROM inventories 
			WHERE unit_id = ? AND date BETWEEN ? AND ?`

		if err := i.db.WithContext(ctx).Raw(query, unitID, startDate, endDate).Scan(&minAvailable).Error; err != nil {
			return nil, err
		}

		// Nếu không có bản ghi, kiểm tra số lượng mặc định của unit
		if minAvailable.MinCount == 0 {
			var unit struct {
				Quantity int
			}
			if err := i.db.Table("units").Select("quantity").Where("id = ?", unitID).First(&unit).Error; err != nil {
				return nil, err
			}
			minAvailable.MinCount = unit.Quantity
		}

		// Sử dụng Redis Pipeline để kiểm tra tất cả các khóa tạm thời cùng lúc
		pipe := i.redisClient.Pipeline()
		cmds := make(map[int]*redis.StringCmd)

		// Đặt tất cả các lệnh GET vào pipeline
		for date := startDate; date <= endDate; date++ {
			key := getTempLockKey(unitID, date)
			cmds[date] = pipe.Get(ctx, key)
		}

		// Thực thi pipeline
		_, err := pipe.Exec(ctx)
		if err != nil && err != redis.Nil {
			log.Error(ctx, "Error executing Redis pipeline", err)
		}

		// Xử lý kết quả từ các lệnh
		lockedQuantity := 0
		for _, cmd := range cmds {
			lockedData, err := cmd.Result()
			if err == nil && lockedData != "" {
				// Có lock, trích xuất số lượng đã khóa
				parts := strings.Split(lockedData, ":")
				if len(parts) == 2 {
					quantity, _ := strconv.Atoi(parts[1])
					if quantity > lockedQuantity {
						lockedQuantity = quantity
					}
				}
			}
		}

		// Tính số lượng thực tế có thể đặt
		availableQuantity := minAvailable.MinCount - lockedQuantity
		if availableQuantity < 0 {
			availableQuantity = 0
		}

		result[unitID] = availableQuantity
	}

	return result, nil
}

func (i InventoryAdapter) BlockInventory(ctx context.Context, tx *gorm.DB, accommodationID int64,
	unitQuantities map[int64]int, startDate, endDate int, note string) error {

	for unitID, quantity := range unitQuantities {
		// Kiểm tra số lượng khả dụng
		for date := startDate; date <= endDate; date++ {
			var inventory model.InventoryModel
			err := tx.WithContext(ctx).Where("unit_id = ? AND date = ?", unitID, date).First(&inventory).Error

			// Nếu không tìm thấy, tạo mới với số lượng từ bảng unit
			if err == gorm.ErrRecordNotFound {
				var unit struct {
					Quantity int
				}
				if err := tx.Table("units").Select("quantity").Where("id = ?", unitID).First(&unit).Error; err != nil {
					return err
				}

				inventory = model.InventoryModel{
					UnitID:          unitID,
					AccommodationID: accommodationID,
					Date:            date,
					AvailableCount:  unit.Quantity,
					TotalCount:      unit.Quantity,
					Status:          constant.AVAILABLE,
				}

				if err := tx.Create(&inventory).Error; err != nil {
					return err
				}
			} else if err != nil {
				return err
			}

			// Kiểm tra số lượng còn đủ không
			if inventory.AvailableCount < quantity {
				return fmt.Errorf("not enough inventory to block for unit %d on date %d", unitID, date)
			}

			// Cập nhật inventory
			if err := tx.Exec(`
				UPDATE inventories 
				SET available_count = available_count - ?,
					status = CASE 
						WHEN (available_count - ?) <= 0 THEN 'blocked'
						ELSE 'partially_booked'
					END,
					note = ?
				WHERE unit_id = ? AND date = ?`,
				quantity, quantity, note, unitID, date).Error; err != nil {
				return err
			}
		}
	}

	return nil
}

func (i InventoryAdapter) UnblockInventory(ctx context.Context, tx *gorm.DB, accommodationID int64,
	unitQuantities map[int64]int, startDate, endDate int) error {

	for unitID, quantity := range unitQuantities {
		for date := startDate; date <= endDate; date++ {
			// Cập nhật inventory
			if err := tx.Exec(`
				UPDATE inventories 
				SET available_count = available_count + ?,
					status = CASE 
						WHEN (available_count + ?) >= total_count THEN 'available'
						ELSE 'partially_blocked'
					END,
					note = CASE 
						WHEN (available_count + ?) >= total_count THEN NULL
						ELSE note
					END
				WHERE unit_id = ? AND date = ? AND status IN ('blocked', 'partially_blocked')`,
				quantity, quantity, quantity, unitID, date).Error; err != nil {
				return err
			}
		}
	}

	return nil
}

func NewInventoryAdapter(db *gorm.DB, redisClient *redis.Client) port.IInventoryPort {
	return &InventoryAdapter{
		db:          db,
		redisClient: redisClient,
		lockTTL:     5 * time.Minute,
	}
}
