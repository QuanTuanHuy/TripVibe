package consumer

import (
	"booking_service/core/domain/constant"
	basedto "booking_service/core/domain/dto/kafka/base"
	"booking_service/core/domain/dto/request"
	"booking_service/core/service"
	"context"
	"encoding/json"
	"github.com/golibs-starter/golib/log"
	"time"
)

type AccommodationEventHandler struct {
	accService service.IAccommodationService
}

func (a *AccommodationEventHandler) HandleMessage(topic string, key, value []byte) {
	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()

	var domainEventDto basedto.DomainEventDto
	if err := json.Unmarshal(value, &domainEventDto); err != nil {
		log.Error(ctx, "error unmarshal domain event dto ", err)
		return
	}

	switch domainEventDto.EventType {
	case constant.DELETE_UNIT:
		a.handleDeleteUnit(ctx, &domainEventDto)
	default:
		log.Error(ctx, "Unknown event", nil)
	}
}

func (a *AccommodationEventHandler) handleDeleteUnit(ctx context.Context, domainEventDto *basedto.DomainEventDto) {
	var deleteUnitDto request.DeleteUnitDto
	dataBytes, err := json.Marshal(domainEventDto.Data)
	if err != nil {
		log.Error(ctx, "error marshal data from domain event ", err)
		return
	}
	if err := json.Unmarshal(dataBytes, &deleteUnitDto); err != nil {
		log.Error(ctx, "error unmarshal data from domain event ", err)
		return
	}

	if err = a.accService.DeleteUnit(ctx, deleteUnitDto.AccommodationId, deleteUnitDto.UnitId); err != nil {
		log.Error(ctx, "error delete unit ", err)
		return
	}
}

func NewAccommodationEventHandler(accService service.IAccommodationService) *AccommodationEventHandler {
	return &AccommodationEventHandler{accService: accService}
}
