package service

import (
	"booking_service/core/domain/dto/request"
	"booking_service/core/domain/entity"
	"booking_service/core/usecase"
	"context"
)

type IAccommodationService interface {
	CreateAccommodation(ctx context.Context, accommodation *entity.AccommodationEntity) (*entity.AccommodationEntity, error)
	GetAccommodationByID(ctx context.Context, ID int64) (*entity.AccommodationEntity, error)
	UpdateAccommodation(ctx context.Context, accommodation *entity.AccommodationEntity) (*entity.AccommodationEntity, error)
	DeleteAccommodation(ctx context.Context, ID int64) error
	AddUnitToAccommodation(ctx context.Context, req *request.AddUnitToAccommodationDto) (*entity.UnitEntity, error)
}

type AccommodationService struct {
	createAccUseCase usecase.ICreateAccommodationUseCase
	getAccUseCase    usecase.IGetAccommodationUseCase
	deleteAccUseCase usecase.IDeleteAccommodationUseCase
	updateAccUseCase usecase.IUpdateAccommodationUseCase
}

func (a AccommodationService) AddUnitToAccommodation(ctx context.Context, req *request.AddUnitToAccommodationDto) (*entity.UnitEntity, error) {
	return a.updateAccUseCase.AddUnitToAccommodation(ctx, req)
}

func (a AccommodationService) CreateAccommodation(ctx context.Context, accommodation *entity.AccommodationEntity) (*entity.AccommodationEntity, error) {
	return a.createAccUseCase.CreateAccommodation(ctx, accommodation)
}

func (a AccommodationService) GetAccommodationByID(ctx context.Context, ID int64) (*entity.AccommodationEntity, error) {
	return a.getAccUseCase.GetAccommodationByID(ctx, ID)
}

func (a AccommodationService) UpdateAccommodation(ctx context.Context, accommodation *entity.AccommodationEntity) (*entity.AccommodationEntity, error) {
	return a.updateAccUseCase.UpdateAccommodationByID(ctx, accommodation)
}

func (a AccommodationService) DeleteAccommodation(ctx context.Context, ID int64) error {
	return a.deleteAccUseCase.DeleteAccommodationByID(ctx, ID)
}

func NewAccommodationService(createAccUseCase usecase.ICreateAccommodationUseCase,
	getAccUseCase usecase.IGetAccommodationUseCase,
	deleteAccUseCase usecase.IDeleteAccommodationUseCase,
	updateAccUseCase usecase.IUpdateAccommodationUseCase) IAccommodationService {
	return &AccommodationService{
		createAccUseCase: createAccUseCase,
		getAccUseCase:    getAccUseCase,
		deleteAccUseCase: deleteAccUseCase,
		updateAccUseCase: updateAccUseCase,
	}
}
