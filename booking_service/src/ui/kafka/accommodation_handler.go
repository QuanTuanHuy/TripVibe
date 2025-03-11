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
	var err error
	if err = json.Unmarshal(value, &kafkaBaseDto); err != nil {
		log.Error("Failed to unmarshal kafka message", err)
		return
	}

	switch kafkaBaseDto.Cmd {
	case constant.CREATE_ACCOMMODATION:
		var req request.CreateAccommodationDto
		dataBytes, err := json.Marshal(kafkaBaseDto.Data)
		if err != nil {
			log.Error("Failed to marshal kafkaBaseDto.Data", err)
			return
		}
		if err := json.Unmarshal(dataBytes, &req); err != nil {
			log.Error("Failed to unmarshal kafka message", err)
			return
		}
		_, err = a.accService.CreateAccommodation(ctx, request.ToAccommodationEntity(&req))
		if err != nil {
			log.Error("Failed to create accommodation", err)
			return
		}
	default:
		log.Error("Unknown command")
	}
}

func NewAccommodationHandler(accService service.IAccommodationService) *AccommodationHandler {
	return &AccommodationHandler{
		accService: accService,
	}
}
