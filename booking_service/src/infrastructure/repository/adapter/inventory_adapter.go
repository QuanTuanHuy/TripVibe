package adapter

import (
	"booking_service/core/domain/constant"
	"booking_service/core/domain/entity"
	"booking_service/core/port"
	"booking_service/infrastructure/repository/mapper"
	"booking_service/infrastructure/repository/model"
	"context"
	"gorm.io/gorm"
	"gorm.io/gorm/clause"
)

type InventoryAdapter struct {
	base
}

func (i InventoryAdapter) GetInventories(
	ctx context.Context,
	accommodation *entity.AccommodationEntity,
	unitIDs []int64,
	startDate,
	endDate int) ([]*entity.InventoryEntity, error) {
	var inventories []*model.InventoryModel
	if err := i.db.WithContext(ctx).
		Where("unit_id IN (?) AND date BETWEEN ? AND ?", unitIDs, startDate, endDate).
		Find(&inventories).Error; err != nil {
		return nil, err
	}

	inventoryEntities := mapper.ToListInventoryEntity(inventories)

	inventoryMap := make(map[int64]map[int]bool)
	for _, unitID := range unitIDs {
		inventoryMap[unitID] = make(map[int]bool)
	}
	for _, inventory := range inventories {
		inventoryMap[inventory.UnitID][inventory.Date] = true
	}
	if len(inventories) == len(unitIDs)*(endDate-startDate+1) {
		return inventoryEntities, nil
	}

	// auto fill inventory
	unitMap := make(map[int64]*entity.UnitEntity)
	for _, unit := range accommodation.Units {
		unitMap[unit.ID] = unit
	}
	for _, unitID := range unitIDs {
		for date := startDate; date <= endDate; date++ {
			if _, ok := inventoryMap[unitID][date]; !ok {
				inventory := &entity.InventoryEntity{
					AccommodationID: accommodation.ID,
					UnitID:          unitID,
					Date:            date,
					AvailableCount:  unitMap[unitID].Quantity,
					Status:          constant.AVAILABLE,
					TotalCount:      unitMap[unitID].Quantity,
				}
				inventoryMap[unitID][date] = true
				inventoryEntities = append(inventoryEntities, inventory)
			}
		}
	}
	return inventoryEntities, nil
}

func (i InventoryAdapter) SaveAll(ctx context.Context, tx *gorm.DB, inventories []*entity.InventoryEntity) error {
	inventoryModels := mapper.ToListInventoryModel(inventories)

	batchSize := constant.BATCH_WRITE_SIZE
	for i := 0; i < len(inventoryModels); i += batchSize {
		end := i + batchSize
		if end > len(inventoryModels) {
			end = len(inventoryModels)
		}

		batch := inventoryModels[i:end]

		if err := tx.WithContext(ctx).Clauses(clause.OnConflict{
			Columns:   []clause.Column{{Name: "id"}},
			DoUpdates: clause.AssignmentColumns([]string{"available_count", "status"}),
		}).Create(&batch).Error; err != nil {
			return err
		}
	}
	return nil
}

func NewInventoryAdapter(db *gorm.DB) port.IInventoryPort {
	return &InventoryAdapter{
		base: base{db: db},
	}
}
