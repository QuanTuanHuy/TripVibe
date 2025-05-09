package response

import "booking_service/core/domain/entity"

type BookingUnitResponse struct {
	ID       int64  `json:"id"`
	UnitID   int64  `json:"unitId"`
	Quantity int    `json:"quantity"`
	FullName string `json:"fullName"`
	Email    string `json:"email"`
	Amount   int64  `json:"amount"`
}

func ToBookingUnitResponse(unit *entity.BookingUnitEntity) *BookingUnitResponse {
	return &BookingUnitResponse{
		ID:       unit.ID,
		UnitID:   unit.UnitID,
		Quantity: unit.Quantity,
		FullName: unit.FullName,
		Email:    unit.Email,
		Amount:   unit.Amount,
	}
}

func ToListBookingUnitResponse(units []*entity.BookingUnitEntity) []*BookingUnitResponse {
	var responses []*BookingUnitResponse
	for _, unit := range units {
		responses = append(responses, ToBookingUnitResponse(unit))
	}
	return responses
}
