package model

import "time"

type QuickBookingModel struct {
	BaseModel
	UserID           int64      `gorm:"column:user_id;index"`
	Name             string     `gorm:"column:name"`
	AccommodationID  int64      `gorm:"column:accommodation_id"`
	NumberOfAdult    int        `gorm:"column:number_of_adult"`
	NumberOfChild    int        `gorm:"column:number_of_child"`
	PreferredUnitIDs string     `gorm:"column:preferred_unit_ids"`
	LastUsed         *time.Time `gorm:"column:last_used"`
}

func (QuickBookingModel) TableName() string {
	return "quick_bookings"
}
