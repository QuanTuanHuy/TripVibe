package mapper

import (
	"booking_service/core/domain/entity"
	"booking_service/infrastructure/repository/model"
)

func ToInventoryModel(inventory *entity.InventoryEntity) *model.InventoryModel {
	if inventory == nil {
		return nil
	}
	return &model.InventoryModel{
		BaseModel: model.BaseModel{
			ID: inventory.ID,
		},
		UnitID:          inventory.UnitID,
		AccommodationID: inventory.AccommodationID,
		Date:            inventory.Date,
		Status:          inventory.Status,
		TotalCount:      inventory.TotalCount,
		AvailableCount:  inventory.AvailableCount,
		Note:            inventory.Note,
	}
}

func ToListInventoryModel(inventories []*entity.InventoryEntity) []*model.InventoryModel {
	var inventoryModels []*model.InventoryModel
	for _, inventory := range inventories {
		inventoryModels = append(inventoryModels, ToInventoryModel(inventory))
	}
	return inventoryModels
}

func ToInventoryEntity(inventory *model.InventoryModel) *entity.InventoryEntity {
	if inventory == nil {
		return nil
	}
	return &entity.InventoryEntity{
		BaseEntity: entity.BaseEntity{
			ID:        inventory.ID,
			CreatedAt: inventory.CreatedAt.Unix(),
			UpdatedAt: inventory.UpdatedAt.Unix(),
		},
		UnitID:          inventory.UnitID,
		AccommodationID: inventory.AccommodationID,
		Date:            inventory.Date,
		Status:          inventory.Status,
		TotalCount:      inventory.TotalCount,
		AvailableCount:  inventory.AvailableCount,
		Note:            inventory.Note,
	}
}

func ToListInventoryEntity(inventoryModels []*model.InventoryModel) []*entity.InventoryEntity {
	var inventoryEntities []*entity.InventoryEntity
	for _, inventoryModel := range inventoryModels {
		inventoryEntities = append(inventoryEntities, ToInventoryEntity(inventoryModel))
	}
	return inventoryEntities
}
