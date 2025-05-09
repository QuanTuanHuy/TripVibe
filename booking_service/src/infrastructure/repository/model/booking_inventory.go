package model

type BookingInventoryModel struct {
	BaseModel
	BookingID int64 `gorm:"column:booking_id;"`
	UnitID    int64 `gorm:"column:unit_id;"`
	Quantity  int   `gorm:"column:quantity;"` // Số lượng đặt
	Date      int   `gorm:"column:date;"`
}

func (BookingInventoryModel) TableName() string {
	return "booking_inventories"
}
