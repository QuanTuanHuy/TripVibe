package request

type InventoryLockRequest struct {
	AccommodationId  int64                       `json:"accommodationId"`
	UserId           int64                       `json:"userId"`
	UnitLockRequests []*InventoryLockUnitRequest `json:"unitLockRequests"`
}

type InventoryLockUnitRequest struct {
	UnitId    int64  `json:"unitId"`
	Quantity  int    `json:"quantity"`
	StartDate string `json:"startDate"`
	EndDate   string `json:"endDate"`
}
