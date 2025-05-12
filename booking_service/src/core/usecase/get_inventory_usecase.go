package usecase

import (
	"booking_service/core/domain/entity"
	"booking_service/core/port"
	"context"
)

type IGetInventoryUseCase interface {
	GetInventories(ctx context.Context, accID int64, unitIDs []int64, startDate, endDate int) ([]*entity.InventoryEntity, error)
}

type GetInventoryUseCase struct {
	inventoryPort port.IInventoryPort
	getAccUseCase IGetAccommodationUseCase
}

func (g GetInventoryUseCase) GetInventories(ctx context.Context, accID int64, unitIDs []int64, startDate, endDate int) ([]*entity.InventoryEntity, error) {
	accommodation, err := g.getAccUseCase.GetAccommodationByID(ctx, accID)
	if err != nil {
		return nil, err
	}

	return g.inventoryPort.GetInventories(ctx, accommodation, unitIDs, startDate, endDate)
}

func NewGetInventoryUseCase(inventoryPort port.IInventoryPort, getAccUseCase IGetAccommodationUseCase) IGetInventoryUseCase {
	return &GetInventoryUseCase{
		inventoryPort: inventoryPort,
		getAccUseCase: getAccUseCase,
	}
}
