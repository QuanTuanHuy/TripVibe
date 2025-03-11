package entity

type BookingEntity struct {
	BaseEntity
	TouristID       int64  `json:"touristId"`
	AccommodationID int64  `json:"accommodationId"`
	NumberOfAdult   int    `json:"numberOfAdult"`
	NumberOfChild   int    `json:"numberOfChild"`
	PaymentID       *int64 `json:"paymentId"`
	CurrencyID      int64  `json:"currencyId"`
	Status          string `json:"status"`
	Note            string `json:"note"`
	StayFrom        int64  `json:"stayFrom"`
	StayTo          int64  `json:"stayTo"`
	InvoiceAmount   int64  `json:"invoiceAmount"`
	FinalAmount     int64  `json:"finalAmount"`

	Units      []*BookingUnitEntity      `json:"units"`
	Promotions []*BookingPromotionEntity `json:"promotions"`
	Tourist    *UserEntity               `json:"tourist"`
}
