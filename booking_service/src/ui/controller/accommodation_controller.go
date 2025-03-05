package controller

import (
	"booking_service/core/domain/common"
	"booking_service/core/domain/dto/request"
	"booking_service/core/service"
	"booking_service/kernel/apihelper"
	"github.com/gin-gonic/gin"
	"github.com/golibs-starter/golib/log"
)

type AccommodationController struct {
	accommodationService service.IAccommodationService
}

func (a AccommodationController) CreateAccommodation(c *gin.Context) {
	req := &request.CreateAccommodationDto{}
	if err := c.ShouldBindJSON(req); err != nil {
		log.Error(c, "error binding request", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	accEntity := request.ToAccommodationEntity(req)
	acc, err := a.accommodationService.CreateAccommodation(c, accEntity)
	if err != nil {
		log.Error(c, "error creating accommodation", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	apihelper.SuccessfulHandle(c, acc)
}

func NewAccommodationController(accommodationService service.IAccommodationService) *AccommodationController {
	return &AccommodationController{
		accommodationService: accommodationService,
	}
}
