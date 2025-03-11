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

type BookingAdapter struct {
	base
}

func (b BookingAdapter) GetBookingByUserIDAndID(ctx context.Context, userID int64, ID int64) (*entity.BookingEntity, error) {
	var bookingModel model.BookingModel
	if err := b.db.WithContext(ctx).Where("id = ? AND tourist_id = ?", ID, userID).
		First(&bookingModel).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, errors.New(constant.ErrBookingNotFound)
		}
		return nil, err
	}
	return mapper.ToBookingEntity(&bookingModel), nil
}

func (b BookingAdapter) CreateBooking(ctx context.Context, tx *gorm.DB, booking *entity.BookingEntity) (*entity.BookingEntity, error) {
	bookingModel := mapper.ToBookingModel(booking)
	if err := tx.WithContext(ctx).Create(&bookingModel).Error; err != nil {
		return nil, err
	}
	return mapper.ToBookingEntity(bookingModel), nil
}

func (b BookingAdapter) GetBookingByID(ctx context.Context, ID int64) (*entity.BookingEntity, error) {
	bookingModel := &model.BookingModel{}
	if err := b.db.WithContext(ctx).Where("id = ?", ID).First(&bookingModel).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, errors.New(constant.ErrBookingNotFound)
		}
		return nil, err
	}
	return mapper.ToBookingEntity(bookingModel), nil
}

func NewBookingAdapter(db *gorm.DB) port.IBookingPort {
	return &BookingAdapter{
		base: base{db: db},
	}
}
