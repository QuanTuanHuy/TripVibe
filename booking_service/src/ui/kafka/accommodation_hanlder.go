package consumer

import (
	"booking_service/core/service"
	"fmt"
)

type AccommodationHandler struct {
	accService service.IAccommodationService
}

func (a AccommodationHandler) HandleMessage(topic string, key, value []byte) {
	fmt.Println("acc handler", "key: ", string(key), "value: ", string(value))
}

func NewAccommodationHandler(accService service.IAccommodationService) *AccommodationHandler {
	return &AccommodationHandler{
		accService: accService,
	}
}
