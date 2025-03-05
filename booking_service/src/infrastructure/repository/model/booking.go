package model

import "time"

type BookingModel struct {
	BaseModel
	TouristID     int64     `gorm:"column:tourist_id"`
	PaymentID     int64     `gorm:"column:payment_id"`
	CurrencyID    int64     `gorm:"column:currency_id"`
	Status        string    `gorm:"column:status"`
	Note          string    `gorm:"column:note"`
	StayFrom      time.Time `gorm:"column:stay_from"`
	StayTo        time.Time `gorm:"column:stay_to"`
	InvoiceAmount int64     `gorm:"column:invoice_amount"`
	FinalAmount   int64     `gorm:"column:final_amount"`
}

func (b BookingModel) TableName() string {
	return "bookings"
}
