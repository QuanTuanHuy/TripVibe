package model

type BookingPromotionModel struct {
	BaseModel
	BookingID     int64  `gorm:"column:booking_id"`
	PromotionID   int64  `gorm:"column:promotion_id"`
	PromotionName string `gorm:"column:promotion_name"`
	Percentage    int    `gorm:"column:percentage"`
}

func (BookingPromotionModel) TableName() string {
	return "bookings_promotions"
}
