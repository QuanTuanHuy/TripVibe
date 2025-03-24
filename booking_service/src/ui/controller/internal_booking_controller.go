package controller

import (
	"booking_service/core/domain/common"
	"booking_service/core/domain/dto/response"
	"booking_service/core/service"
	"booking_service/kernel/apihelper"
	"strconv"

	"github.com/gin-gonic/gin"
	"github.com/golibs-starter/golib/log"
)

type InternalBookingController struct {
	bookingService service.IBookingService
}

func (b *InternalBookingController) GetCompletedBookingByUserIdAndUnitId(c *gin.Context) {
	userIdStr := c.Query("userId")
	unitIdStr := c.Query("unitId")

	userId, err := strconv.ParseInt(userIdStr, 10, 64)
	if err != nil {
		log.Error(c, "Error parsing userId: ", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	unitId, err := strconv.ParseInt(unitIdStr, 10, 64)
	if err != nil {
		log.Error(c, "Error parsing unitId: ", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	booking, err := b.bookingService.GetCompletedBookingByUserIdAndUnitId(c, userId, unitId)
	if err != nil {
		log.Error(c, "Error getting booking: ", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	apihelper.SuccessfulHandle(c, response.ToBookingInfoResponse(booking))
}

func NewInternalBookingController(bookingService service.IBookingService) *InternalBookingController {
	return &InternalBookingController{
		bookingService: bookingService,
	}
}
