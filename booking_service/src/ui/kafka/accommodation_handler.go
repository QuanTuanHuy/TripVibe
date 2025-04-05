package consumer

import (
	"booking_service/core/domain/constant"
	basedto "booking_service/core/domain/dto/kafka/base"
	"booking_service/core/domain/dto/request"
	"booking_service/core/service"
	"context"
	"encoding/json"
	"github.com/golibs-starter/golib/log"
)

type AccommodationHandler struct {
	accService service.IAccommodationService
}

func (a AccommodationHandler) HandleMessage(topic string, key, value []byte) {
	ctx := context.Background()

	var kafkaBaseDto basedto.KafkaBaseDto
	if err := json.Unmarshal(value, &kafkaBaseDto); err != nil {
		log.Error(ctx, "Failed to unmarshal kafka message", err)
		return
	}

	switch kafkaBaseDto.Cmd {
	case constant.CREATE_ACCOMMODATION:
		a.handleCreateAccommodation(ctx, &kafkaBaseDto)
	case constant.ADD_UNIT_TO_ACC:
		a.handleAddUnitToAccommodation(ctx, &kafkaBaseDto)
	default:
		log.Error(ctx, "Unknown command", nil)
	}
}

func (a AccommodationHandler) handleCreateAccommodation(ctx context.Context, kafkaBaseDto *basedto.KafkaBaseDto) {
	var req request.CreateAccommodationDto
	dataBytes, err := json.Marshal(kafkaBaseDto.Data)
	if err != nil {
		log.Error(ctx, "Failed to marshal kafkaBaseDto.Data", err)
		return
	}

	if err := json.Unmarshal(dataBytes, &req); err != nil {
		log.Error(ctx, "Failed to unmarshal create accommodation data", err)
		return
	}

	accommodation, err := a.accService.CreateAccommodation(ctx, request.ToAccommodationEntity(&req))
	if err != nil {
		log.Error(ctx, "Failed to create accommodation", err)
		return
	}

	log.Info(ctx, "Successfully created accommodation with ID:", accommodation.ID)
}

func (a AccommodationHandler) handleAddUnitToAccommodation(ctx context.Context, kafkaBaseDto *basedto.KafkaBaseDto) {
	var req request.AddUnitToAccommodationDto
	dataBytes, err := json.Marshal(kafkaBaseDto.Data)
	if err != nil {
		log.Error(ctx, "Failed to marshal kafkaBaseDto.Data", err)
		return
	}

	if err := json.Unmarshal(dataBytes, &req); err != nil {
		log.Error(ctx, "Failed to unmarshal add unit data", err)
		return
	}

	unit, err := a.accService.AddUnitToAccommodation(ctx, &req)
	if err != nil {
		log.Error(ctx, "Failed to add unit to accommodation", err)
		return
	}

	log.Info(ctx, "Successfully added unit with ID:", unit.ID, "to accommodation:", req.AccommodationID)
}

func NewAccommodationHandler(accService service.IAccommodationService) *AccommodationHandler {
	return &AccommodationHandler{
		accService: accService,
	}
}
