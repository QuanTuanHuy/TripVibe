package adapter

import (
	"booking_service/core/domain/constant"
	"booking_service/core/domain/entity"
	"booking_service/core/port"
	"booking_service/infrastructure/repository/mapper"
	"booking_service/infrastructure/repository/model"
	"context"
	"errors"
	"gorm.io/gorm"
)

type BookingPromotionAdapter struct {
	base
}

func (b BookingPromotionAdapter) GetBookingPromotionsByBookingIDs(ctx context.Context, bookingIDs []int64) ([]*entity.BookingPromotionEntity, error) {
	var bookingPromotionModels []*model.BookingPromotionModel
	if err := b.db.WithContext(ctx).Model(&model.BookingPromotionModel{}).
		Where("booking_id IN (?)", bookingIDs).
		Find(&bookingPromotionModels).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, errors.New(constant.ErrBookingPromotionNotFound)
		}
	}
	return mapper.ToListBookingPromotionEntity(bookingPromotionModels), nil
}

func (b BookingPromotionAdapter) CreateBookingPromotions(ctx context.Context, tx *gorm.DB, bookingPromotions []*entity.BookingPromotionEntity) ([]*entity.BookingPromotionEntity, error) {
	bookingPromotionModels := mapper.ToListBookingPromotionModel(bookingPromotions)
	if err := tx.WithContext(ctx).Create(&bookingPromotionModels).Error; err != nil {
		return nil, err
	}
	return mapper.ToListBookingPromotionEntity(bookingPromotionModels), nil
}

func (b BookingPromotionAdapter) GetBookingPromotionsByBookingID(ctx context.Context, bookingID int64) ([]*entity.BookingPromotionEntity, error) {
	var bookingPromotionModels []*model.BookingPromotionModel
	if err := b.db.WithContext(ctx).Model(&model.BookingPromotionModel{}).
		Where("booking_id = ?", bookingID).
		Find(&bookingPromotionModels).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, errors.New(constant.ErrBookingPromotionNotFound)
		}
		return nil, err
	}
	return mapper.ToListBookingPromotionEntity(bookingPromotionModels), nil
}

func NewBookingPromotionAdapter(db *gorm.DB) port.IBookingPromotionPort {
	return &BookingPromotionAdapter{
		base: base{db: db},
	}
}
