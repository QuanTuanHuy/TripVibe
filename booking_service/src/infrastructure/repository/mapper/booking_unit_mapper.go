package mapper

import (
	"booking_service/core/domain/entity"
	"booking_service/infrastructure/repository/model"
)

func ToBookingUnitEntity(bookingUnit *model.BookingUnitModel) *entity.BookingUnitEntity {
	return &entity.BookingUnitEntity{
		BaseEntity: entity.BaseEntity{
			ID:        bookingUnit.ID,
			CreatedAt: bookingUnit.CreatedAt.Unix(),
			UpdatedAt: bookingUnit.UpdatedAt.Unix(),
		},
		BookingID: bookingUnit.BookingID,
		UnitID:    bookingUnit.UnitID,
		FullName:  bookingUnit.FullName,
		Email:     bookingUnit.Email,
		Amount:    bookingUnit.Amount,
	}
}

func ToBookingUnitModel(bookingUnit *entity.BookingUnitEntity) *model.BookingUnitModel {
	return &model.BookingUnitModel{
		BaseModel: model.BaseModel{
			ID: bookingUnit.ID,
		},
		BookingID: bookingUnit.BookingID,
		UnitID:    bookingUnit.UnitID,
		FullName:  bookingUnit.FullName,
		Email:     bookingUnit.Email,
		Amount:    bookingUnit.Amount,
	}
}

func ToListBookingUnitEntity(bookingUnitModels []*model.BookingUnitModel) []*entity.BookingUnitEntity {
	var bookingUnitEntities []*entity.BookingUnitEntity
	for _, bookingUnitModel := range bookingUnitModels {
		bookingUnitEntities = append(bookingUnitEntities, ToBookingUnitEntity(bookingUnitModel))
	}
	return bookingUnitEntities
}

func ToListBookingUnitModel(bookingUnitEntities []*entity.BookingUnitEntity) []*model.BookingUnitModel {
	var bookingUnitModels []*model.BookingUnitModel
	for _, bookingUnitEntity := range bookingUnitEntities {
		bookingUnitModels = append(bookingUnitModels, ToBookingUnitModel(bookingUnitEntity))
	}
	return bookingUnitModels
}
