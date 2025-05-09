package adapter

import (
	"booking_service/core/domain/constant"
	"booking_service/core/domain/dto/request"
	"booking_service/core/domain/entity"
	"booking_service/core/port"
	"booking_service/infrastructure/repository/mapper"
	"booking_service/infrastructure/repository/model"
	"context"
	"errors"
	"gorm.io/gorm"
	"time"
)

type QuickBookingAdapter struct {
	base
}

func NewQuickBookingAdapter(db *gorm.DB) port.IQuickBookingPort {
	return &QuickBookingAdapter{
		base: base{
			db: db,
		},
	}
}

func (q QuickBookingAdapter) CreateQuickBooking(ctx context.Context, tx *gorm.DB, entity *entity.QuickBookingEntity) (*entity.QuickBookingEntity, error) {
	quickBookingModel := mapper.ToQuickBookingModel(entity)
	if err := tx.WithContext(ctx).Create(quickBookingModel).Error; err != nil {
		return nil, err
	}
	return mapper.ToQuickBookingEntity(quickBookingModel), nil
}

func (q QuickBookingAdapter) UpdateQuickBooking(ctx context.Context, tx *gorm.DB, entity *entity.QuickBookingEntity) (*entity.QuickBookingEntity, error) {
	quickBookingModel := mapper.ToQuickBookingModel(entity)
	if err := tx.WithContext(ctx).Where("id = ?", entity.ID).
		Updates(quickBookingModel).Error; err != nil {
		return nil, err
	}
	return entity, nil
}

func (q QuickBookingAdapter) DeleteQuickBooking(ctx context.Context, tx *gorm.DB, id int64, userID int64) error {
	if err := tx.WithContext(ctx).
		Where("id = ? AND user_id = ?", id, userID).
		Delete(&model.QuickBookingModel{}).Error; err != nil {
		return err
	}
	return nil
}

func (q QuickBookingAdapter) GetQuickBooking(ctx context.Context, id int64, userID int64) (*entity.QuickBookingEntity, error) {
	var quickBooking model.QuickBookingModel
	if err := q.db.WithContext(ctx).Where("id = ? AND user_id = ?", id, userID).
		First(&quickBooking).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, errors.New(constant.ErrQuickBookingNotFound)
		}
	}
	return mapper.ToQuickBookingEntity(&quickBooking), nil
}

func (q QuickBookingAdapter) GetQuickBookings(ctx context.Context, userID int64, params *request.QuickBookingParams) ([]*entity.QuickBookingEntity, int64, error) {
	query := q.db.WithContext(ctx).
		Model(&model.QuickBookingModel{}).
		Where("user_id = ?", userID)

	if params.AccommodationID != nil {
		query = query.Where("accommodation_id = ?", *params.AccommodationID)
	}

	var total int64
	if err := query.Count(&total).Error; err != nil {
		return nil, 0, err
	}

	// Apply pagination
	page := 0
	size := 10
	if params.Page != nil {
		page = *params.Page
	}
	if params.PageSize != nil {
		size = *params.PageSize
	}
	offset := page * size

	// Get records with pagination
	var quickBookings []*model.QuickBookingModel
	if err := query.
		Order("last_used DESC NULLS LAST, created_at DESC").
		Offset(offset).
		Limit(size).
		Find(&quickBookings).Error; err != nil {
		return nil, 0, err
	}

	return mapper.ToListQuickBookingEntity(quickBookings), total, nil
}

func (q QuickBookingAdapter) UpdateLastUsed(ctx context.Context, tx *gorm.DB, id int64, userID int64) error {
	now := time.Now()
	return tx.WithContext(ctx).Model(&model.QuickBookingModel{}).
		Where("id = ? AND user_id = ?", id, userID).
		Update("last_used", now).Error
}
