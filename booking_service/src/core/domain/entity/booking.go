package entity

type BookingEntity struct {
	BaseEntity
	TouristID     int64  `json:"touristId"`
	PaymentID     int64  `json:"paymentId"`
	CurrencyID    int64  `json:"currencyId"`
	Status        string `json:"status"`
	Note          string `json:"note"`
	StayFrom      int64  `json:"stayFrom"`
	StayTo        int64  `json:"stayTo"`
	InvoiceAmount int64  `json:"invoiceAmount"`
	FinalAmount   int64  `json:"finalAmount"`

	Units []*BookingUnitEntity `json:"units"`
}
