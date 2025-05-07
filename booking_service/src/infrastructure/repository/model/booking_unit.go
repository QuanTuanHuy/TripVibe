package model

type BookingUnitModel struct {
	BaseModel
	BookingID int64  `gorm:"column:booking_id"`
	Quantity  int    `gorm:"column:quantity"`
	UnitID    int64  `gorm:"column:unit_id"`
	FullName  string `gorm:"column:full_name"`
	Email     string `gorm:"column:email"`
	Amount    int64  `gorm:"column:amount"`
}

func (BookingUnitModel) TableName() string {
	return "bookings_units"
}
