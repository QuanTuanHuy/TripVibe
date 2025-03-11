package controller

import (
	"booking_service/core/domain/common"
	"booking_service/core/domain/dto/request"
	"booking_service/core/service"
	"booking_service/kernel/apihelper"
	"github.com/gin-gonic/gin"
	"github.com/golibs-starter/golib/log"
)

type BookingController struct {
	bookingService service.IBookingService
}

func (b *BookingController) CreateBooking(c *gin.Context) {
	var req request.CreateBookingDto
	if err := c.ShouldBindJSON(&req); err != nil {
		log.Error(c, "error binding request ", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	booking, err := b.bookingService.CreateBooking(c, &req)
	if err != nil {
		log.Error(c, "error creating booking ", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	apihelper.SuccessfulHandle(c, booking)
}

func NewBookingController(bookingService service.IBookingService) *BookingController {
	return &BookingController{
		bookingService: bookingService,
	}
}
