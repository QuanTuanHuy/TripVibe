package response

import "booking_service/core/domain/entity"

type BookingPromotionResponse struct {
	ID            int64  `json:"id"`
	PromotionID   int64  `json:"promotionId"`
	PromotionName string `json:"promotionName"`
	Percentage    int    `json:"percentage"`
}

func ToBookingPromotionResponse(promotion *entity.BookingPromotionEntity) *BookingPromotionResponse {
	return &BookingPromotionResponse{
		ID:            promotion.ID,
		PromotionID:   promotion.PromotionID,
		PromotionName: promotion.PromotionName,
		Percentage:    promotion.Percentage,
	}
}

func ToListBookingPromotionResponse(promotions []*entity.BookingPromotionEntity) []*BookingPromotionResponse {
	var responses []*BookingPromotionResponse
	for _, promotion := range promotions {
		responses = append(responses, ToBookingPromotionResponse(promotion))
	}
	return responses
}
