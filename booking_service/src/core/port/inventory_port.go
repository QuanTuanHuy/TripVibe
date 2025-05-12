package port

import (
	"booking_service/core/domain/entity"
	"context"
	"gorm.io/gorm"
)

type IInventoryPort interface {
	GetInventories(
		ctx context.Context,
		accommodation *entity.AccommodationEntity,
		unitIDs []int64,
		startDate,
		endDate int) ([]*entity.InventoryEntity, error)

	SaveAll(ctx context.Context, tx *gorm.DB, inventories []*entity.InventoryEntity) error
}
