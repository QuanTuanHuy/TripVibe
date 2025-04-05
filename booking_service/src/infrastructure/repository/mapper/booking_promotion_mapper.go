package mapper

import (
	"booking_service/core/domain/entity"
	"booking_service/infrastructure/repository/model"
)

func ToBookingPromotionModel(bookingPromotion *entity.BookingPromotionEntity) *model.BookingPromotionModel {
	return &model.BookingPromotionModel{
		BaseModel: model.BaseModel{
			ID: bookingPromotion.ID,
		},
		PromotionID:   bookingPromotion.PromotionID,
		PromotionName: bookingPromotion.PromotionName,
		BookingID:     bookingPromotion.BookingID,
		Percentage:    bookingPromotion.Percentage,
	}
}

func ToBookingPromotionEntity(bookingPromotion *model.BookingPromotionModel) *entity.BookingPromotionEntity {
	return &entity.BookingPromotionEntity{
		BaseEntity: entity.BaseEntity{
			ID:        bookingPromotion.ID,
			CreatedAt: bookingPromotion.CreatedAt.Unix(),
			UpdatedAt: bookingPromotion.UpdatedAt.Unix(),
		},
		PromotionID:   bookingPromotion.PromotionID,
		PromotionName: bookingPromotion.PromotionName,
		BookingID:     bookingPromotion.BookingID,
		Percentage:    bookingPromotion.Percentage,
	}
}

func ToListBookingPromotionModel(bookingPromotions []*entity.BookingPromotionEntity) []*model.BookingPromotionModel {
	bookingPromotionModels := make([]*model.BookingPromotionModel, len(bookingPromotions))
	for i, bookingPromotion := range bookingPromotions {
		bookingPromotionModels[i] = ToBookingPromotionModel(bookingPromotion)
	}
	return bookingPromotionModels
}

func ToListBookingPromotionEntity(bookingPromotionModels []*model.BookingPromotionModel) []*entity.BookingPromotionEntity {
	bookingPromotions := make([]*entity.BookingPromotionEntity, len(bookingPromotionModels))
	for i, bookingPromotionModel := range bookingPromotionModels {
		bookingPromotions[i] = ToBookingPromotionEntity(bookingPromotionModel)
	}
	return bookingPromotions
}
