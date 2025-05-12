package model

type InventoryModel struct {
	BaseModel
	UnitID          int64  `gorm:"column:unit_id;"`
	AccommodationID int64  `gorm:"column:accommodation_id;"`
	AvailableCount  int    `gorm:"column:available_count;"`
	TotalCount      int    `gorm:"column:total_count;"`
	Date            int    `gorm:"column:date;"`
	Status          string `gorm:"column:status;"` // AVAILABLE, PARTIALLY_BOOKED, FULLY_BOOKED, BLOCKED, PARTIALLY_BLOCKED
	Note            string `gorm:"column:note"`
}

func (InventoryModel) TableName() string {
	return "inventories"
}
