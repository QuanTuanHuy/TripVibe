package port

import (
	"booking_service/core/domain/dto/request"
	"booking_service/core/domain/dto/response"
	"context"
)

type IPromotionPort interface {
	VerifyPromotion(ctx context.Context, req *request.VerifyPromotionRequest) (*response.VerifyPromotionResponse, error)
}
