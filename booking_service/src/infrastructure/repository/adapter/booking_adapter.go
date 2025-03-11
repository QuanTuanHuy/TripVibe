package adapter

import (
	"booking_service/core/domain/constant"
	"booking_service/core/domain/dto/request"
	"booking_service/core/domain/entity"
	"booking_service/core/port"
	"booking_service/infrastructure/repository/mapper"
	"booking_service/infrastructure/repository/model"
	"booking_service/infrastructure/repository/specification"
	"context"
	"errors"
	"gorm.io/gorm"
)

type BookingAdapter struct {
	base
}

func (b BookingAdapter) GetAllBookings(ctx context.Context, params *request.BookingParams) ([]*entity.BookingEntity, error) {
	var bookingModels []*model.BookingModel
	query, args := specification.ToGetBookingSpecification(params)
	if err := b.db.WithContext(ctx).
		Raw(query, args...).
		Find(&bookingModels).Error; err != nil {
		return nil, err
	}
	return mapper.ToListBookingEntity(bookingModels), nil
}

func (b BookingAdapter) CountAllBookings(ctx context.Context, params *request.BookingParams) (int64, error) {
	var count int64
	query, args := specification.ToCountBookingSpecification(params)
	if err := b.db.WithContext(ctx).
		Raw(query, args...).
		Count(&count).Error; err != nil {
		return 0, err
	}
	return count, nil
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
