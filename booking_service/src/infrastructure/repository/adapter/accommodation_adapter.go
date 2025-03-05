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

type AccommodationAdapter struct {
	base
}

func (a AccommodationAdapter) DeleteAccommodation(ctx context.Context, tx *gorm.DB, ID int64) error {
	if err := tx.WithContext(ctx).Where("id = ?", ID).Delete(&model.AccommodationModel{}).Error; err != nil {
		return err
	}
	return nil
}

func (a AccommodationAdapter) UpdateAccommodation(ctx context.Context, tx *gorm.DB, accommodation *entity.AccommodationEntity) (*entity.AccommodationEntity, error) {
	accModel := mapper.ToAccommodationModel(accommodation)
	if err := tx.WithContext(ctx).Model(&model.AccommodationModel{}).
		Where("id = ?", accModel.ID).
		Updates(accModel).Error; err != nil {
		return nil, err
	}
	return mapper.ToAccommodationEntity(accModel), nil
}

func (a AccommodationAdapter) GetAccommodationByID(ctx context.Context, ID int64) (*entity.AccommodationEntity, error) {
	accModel := &model.AccommodationModel{}
	if err := a.db.WithContext(ctx).Where("id = ?", ID).First(&accModel).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, errors.New(constant.ErrAccommodationNotFound)
		}
	}
	return mapper.ToAccommodationEntity(accModel), nil
}

func (a AccommodationAdapter) CreateAccommodation(ctx context.Context, tx *gorm.DB, accommodation *entity.AccommodationEntity) (*entity.AccommodationEntity, error) {
	accommodationModel := mapper.ToAccommodationModel(accommodation)
	if err := tx.WithContext(ctx).Create(accommodationModel).Error; err != nil {
		return nil, err
	}
	return mapper.ToAccommodationEntity(accommodationModel), nil
}

func NewAccommodationAdapter(db *gorm.DB) port.IAccommodationPort {
	return &AccommodationAdapter{
		base: base{db: db},
	}
}
