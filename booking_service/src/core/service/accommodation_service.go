package service

import (
	"booking_service/core/domain/entity"
	"booking_service/core/usecase"
	"context"
)

type IAccommodationService interface {
	CreateAccommodation(ctx context.Context, accommodation *entity.AccommodationEntity) (*entity.AccommodationEntity, error)
	GetAccommodationByID(ctx context.Context, ID int64) (*entity.AccommodationEntity, error)
	UpdateAccommodation(ctx context.Context, ID int64, accommodation *entity.AccommodationEntity) (*entity.AccommodationEntity, error)
	DeleteAccommodation(ctx context.Context, ID int64) error
}

type AccommodationService struct {
	createAccUseCase usecase.ICreateAccommodationUseCase
	getAccUseCase    usecase.IGetAccommodationUseCase
}

func (a AccommodationService) CreateAccommodation(ctx context.Context, accommodation *entity.AccommodationEntity) (*entity.AccommodationEntity, error) {
	return a.createAccUseCase.CreateAccommodation(ctx, accommodation)
}

func (a AccommodationService) GetAccommodationByID(ctx context.Context, ID int64) (*entity.AccommodationEntity, error) {
	return a.getAccUseCase.GetAccommodationById(ctx, ID)
}

func (a AccommodationService) UpdateAccommodation(ctx context.Context, ID int64, accommodation *entity.AccommodationEntity) (*entity.AccommodationEntity, error) {
	//TODO implement me
	panic("implement me")
}

func (a AccommodationService) DeleteAccommodation(ctx context.Context, ID int64) error {
	//TODO implement me
	panic("implement me")
}

func NewAccommodationService(createAccUseCase usecase.ICreateAccommodationUseCase,
	getAccUseCase usecase.IGetAccommodationUseCase) IAccommodationService {
	return &AccommodationService{
		createAccUseCase: createAccUseCase,
		getAccUseCase:    getAccUseCase,
	}
}
