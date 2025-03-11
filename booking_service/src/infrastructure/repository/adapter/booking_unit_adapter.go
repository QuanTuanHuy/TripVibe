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

type BookingUnitAdapter struct {
	base
}

func (b BookingUnitAdapter) GetBookingUnitsByBookingIDs(ctx context.Context, bookingIDs []int64) ([]*entity.BookingUnitEntity, error) {
	var bookingUnitModels []*model.BookingUnitModel
	if err := b.db.WithContext(ctx).Model(&model.BookingUnitModel{}).
		Where("booking_id IN (?)", bookingIDs).Find(&bookingUnitModels).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, errors.New(constant.ErrBookingUnitNotFound)
		}
	}
	return mapper.ToListBookingUnitEntity(bookingUnitModels), nil
}

func (b BookingUnitAdapter) CreateBookingUnits(ctx context.Context, tx *gorm.DB, bookingUnits []*entity.BookingUnitEntity) ([]*entity.BookingUnitEntity, error) {
	bookingUnitModels := mapper.ToListBookingUnitModel(bookingUnits)
	if err := tx.WithContext(ctx).Create(bookingUnitModels).Error; err != nil {
		return nil, err
	}
	return mapper.ToListBookingUnitEntity(bookingUnitModels), nil
}

func (b BookingUnitAdapter) GetBookingUnitsByBookingID(ctx context.Context, bookingID int64) ([]*entity.BookingUnitEntity, error) {
	var bookingUnitModels []*model.BookingUnitModel
	if err := b.db.WithContext(ctx).Model(&model.BookingUnitModel{}).
		Where("booking_id = ?", bookingID).Find(&bookingUnitModels).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, errors.New(constant.ErrBookingUnitNotFound)
		}
	}
	return mapper.ToListBookingUnitEntity(bookingUnitModels), nil
}

func NewBookingUnitAdapter(db *gorm.DB) port.IBookingUnitPort {
	return &BookingUnitAdapter{
		base: base{db: db},
	}
}
