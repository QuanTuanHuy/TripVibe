package mapper

import (
	"booking_service/core/domain/entity"
	"booking_service/infrastructure/repository/model"
	"time"
)

func ToQuickBookingModel(quickBooking *entity.QuickBookingEntity) *model.QuickBookingModel {
	if quickBooking == nil {
		return nil
	}
	var lastUsed time.Time
	if quickBooking.LastUsed != nil {
		lastUsed = time.Unix(*quickBooking.LastUsed, 0)
	}
	return &model.QuickBookingModel{
		BaseModel: model.BaseModel{
			ID: quickBooking.ID,
		},
		AccommodationID:  quickBooking.AccommodationID,
		Name:             quickBooking.Name,
		UserID:           quickBooking.UserID,
		NumberOfChild:    quickBooking.NumberOfChild,
		NumberOfAdult:    quickBooking.NumberOfAdult,
		PreferredUnitIDs: quickBooking.PreferredUnitIDs,
		LastUsed:         &lastUsed,
	}
}

func ToQuickBookingEntity(quickBooking *model.QuickBookingModel) *entity.QuickBookingEntity {
	if quickBooking == nil {
		return nil
	}
	var lastUsed int64
	if quickBooking.LastUsed != nil {
		lastUsed = quickBooking.LastUsed.Unix()
	}
	return &entity.QuickBookingEntity{
		BaseEntity: entity.BaseEntity{
			ID:        quickBooking.ID,
			CreatedAt: quickBooking.CreatedAt.Unix(),
			UpdatedAt: quickBooking.UpdatedAt.Unix(),
		},
		UserID:           quickBooking.UserID,
		Name:             quickBooking.Name,
		AccommodationID:  quickBooking.AccommodationID,
		NumberOfChild:    quickBooking.NumberOfChild,
		NumberOfAdult:    quickBooking.NumberOfAdult,
		PreferredUnitIDs: quickBooking.PreferredUnitIDs,
		LastUsed:         &lastUsed,
	}
}

func ToListQuickBookingEntity(quickBookings []*model.QuickBookingModel) []*entity.QuickBookingEntity {
	var list []*entity.QuickBookingEntity
	for _, q := range quickBookings {
		list = append(list, ToQuickBookingEntity(q))
	}
	return list
}
