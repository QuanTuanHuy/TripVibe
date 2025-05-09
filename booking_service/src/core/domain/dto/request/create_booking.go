package request

type CreateBookingDto struct {
	AccommodationID    int64               `json:"accommodationId"`
	CurrencyID         int64               `json:"currencyId"`
	NumberOfAdult      int                 `json:"numberOfAdult"`
	NumberOfChild      int                 `json:"numberOfChild"`
	Note               string              `json:"note"`
	StayFrom           int64               `json:"stayFrom"`
	StayTo             int64               `json:"stayTo"`
	InvoiceAmount      int64               `json:"invoiceAmount"`
	FinalInvoiceAmount int64               `json:"finalInvoiceAmount"`
	Tourist            *Tourist            `json:"tourist"`
	Units              []*BookingUnit      `json:"units"`
	Promotions         []*BookingPromotion `json:"promotions"`
}

type Tourist struct {
	TouristID int64  `json:"touristId"`
	LastName  string `json:"lastName"`
	FirstName string `json:"firstName"`
	Email     string `json:"email"`
}

type BookingUnit struct {
	UnitID   int64  `json:"unitId"`
	Quantity int    `json:"quantity"`
	FullName string `json:"fullName"`
	Email    string `json:"email"`
	Amount   int64  `json:"amount"`
}

type BookingPromotion struct {
	PromotionID        int64  `json:"promotionId"`
	PromotionName      string `json:"promotionName"`
	DiscountPercentage int    `json:"discountPercentage"`
}
