package controller

import (
	"booking_service/core/domain/common"
	"booking_service/core/domain/constant"
	"booking_service/core/domain/dto/request"
	"booking_service/core/service"
	"booking_service/kernel/apihelper"
	"strconv"

	"github.com/gin-gonic/gin"
	"github.com/golibs-starter/golib/log"
)

type AccommodationController struct {
	accommodationService service.IAccommodationService
}

func (a AccommodationController) CreateAccommodation(c *gin.Context) {
	userID, ok := c.Get("userID")
	if !ok {
		log.Error(c, "error getting user id from context")
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	req := &request.CreateAccommodationDto{}
	if err := c.ShouldBindJSON(req); err != nil {
		log.Error(c, "error binding request", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	accEntity := request.ToAccommodationEntity(req)
	accEntity.OwnerID = userID.(int64)
	acc, err := a.accommodationService.CreateAccommodation(c, accEntity)
	if err != nil {
		log.Error(c, "error creating accommodation", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	apihelper.SuccessfulHandle(c, acc)
}

func (a AccommodationController) GetAccommodationDetail(c *gin.Context) {
	ID, err := strconv.ParseInt(c.Param("id"), 10, 64)
	if err != nil {
		log.Error(c, "error binding request", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	acc, err := a.accommodationService.GetAccommodationByID(c, ID)
	if err != nil {
		log.Error(c, "error getting accommodation", err)
		if err.Error() == constant.ErrAccommodationNotFound {
			apihelper.AbortErrorHandle(c, common.ErrCodeAccommodationNotFound)
			return
		}
		apihelper.AbortErrorHandle(c, common.GeneralServiceUnavailable)
		return
	}

	apihelper.SuccessfulHandle(c, acc)
}

func (a AccommodationController) DeleteAccommodationByID(c *gin.Context) {
	ID, err := strconv.ParseInt(c.Param("id"), 10, 64)
	if err != nil {
		log.Error(c, "error binding request", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	err = a.accommodationService.DeleteAccommodation(c, ID)
	if err != nil {
		if err.Error() == constant.ErrAccommodationNotFound {
			apihelper.AbortErrorHandle(c, common.ErrCodeAccommodationNotFound)
			return
		}
		apihelper.AbortErrorHandle(c, common.GeneralServiceUnavailable)
		return
	}

	apihelper.SuccessfulHandle(c, nil)
}

func (a AccommodationController) UpdateAccommodation(c *gin.Context) {
	var req request.UpdateAccommodationDto
	if err := c.ShouldBindJSON(&req); err != nil {
		log.Error(c, "error binding request", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	accEntity := request.FromUpdateDtoToAccommodationEntity(req)
	acc, err := a.accommodationService.UpdateAccommodation(c, accEntity)
	if err != nil {
		log.Error(c, "error updating accommodation", err)
		apihelper.AbortErrorHandle(c, common.GeneralServiceUnavailable)
		return
	}

	apihelper.SuccessfulHandle(c, acc)
}

func NewAccommodationController(accommodationService service.IAccommodationService) *AccommodationController {
	return &AccommodationController{
		accommodationService: accommodationService,
	}
}
