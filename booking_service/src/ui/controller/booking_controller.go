package controller

import (
	"booking_service/core/domain/common"
	"booking_service/core/domain/constant"
	"booking_service/core/domain/dto/request"
	"booking_service/core/domain/dto/response"
	"booking_service/core/service"
	"booking_service/kernel/apihelper"
	"strconv"

	"github.com/gin-gonic/gin"
	"github.com/golibs-starter/golib/log"
)

type BookingController struct {
	bookingService service.IBookingService
}

func (b *BookingController) GetAllBookings(c *gin.Context) {
	var params request.BookingParams

	if err := c.ShouldBindQuery(&params); err != nil {
		log.Error(c, "error binding request ", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	if params.Page == nil {
		defaultPage := constant.DefaultOffset
		params.Page = &defaultPage
	}
	if params.PageSize == nil {
		defaultPageSize := constant.DefaultPageSize
		params.PageSize = &defaultPageSize
	}

	getBookingResponse, err := b.bookingService.GetAllBookings(c, &params)
	if err != nil {
		log.Error(c, "error getting bookings", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}
	apihelper.SuccessfulHandle(c, getBookingResponse)
}

func (b *BookingController) GetDetailBooking(c *gin.Context) {
	userID, ok := c.Get("userID")
	if !ok {
		log.Error(c, "error getting user id from context")
		return
	}

	bookingID, err := strconv.ParseInt(c.Param("id"), 10, 64)
	if err != nil {
		log.Error(c, "error binding request ", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	booking, err := b.bookingService.GetDetailBooking(c, userID.(int64), bookingID)
	if err != nil {
		log.Error(c, "error getting booking ", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	apihelper.SuccessfulHandle(c, response.ToBookingResponse(booking))
}

func (b *BookingController) CreateBooking(c *gin.Context) {
	userID, ok := c.Get("userID")
	if !ok {
		log.Error(c, "error getting user id from context")
		return
	}

	var req request.CreateBookingDto
	if err := c.ShouldBindJSON(&req); err != nil {
		log.Error(c, "error binding request ", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}
	req.Tourist.TouristID = userID.(int64)

	booking, err := b.bookingService.CreateBooking(c, &req)
	if err != nil {
		log.Error(c, "error creating booking ", err)
		if err.Error() == constant.ErrUnitQuantityExceedsAvailable {
			apihelper.AbortErrorHandle(c, common.ErrUnitQuantityExceedsAvailable)
			return
		}
		if err.Error() == constant.ErrInventoryNoLongerAvailable {
			apihelper.AbortErrorHandle(c, common.ErrInventoryNoLongerAvailable)
			return
		}
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	apihelper.SuccessfulHandle(c, response.ToBookingResponse(booking))
}

func (b *BookingController) ConfirmBooking(c *gin.Context) {
	_, ok := c.Get("userID")
	if !ok {
		log.Error(c, "error getting user id from context")
		return
	}

	bookingID, err := strconv.ParseInt(c.Param("id"), 10, 64)
	if err != nil {
		log.Error(c, "error binding request ", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	confirmResponse, err := b.bookingService.ConfirmBooking(c, bookingID)
	if err != nil {
		log.Error(c, "error confirming booking ", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	apihelper.SuccessfulHandle(c, confirmResponse)
}

func (b *BookingController) ApproveBooking(c *gin.Context) {
	userID, ok := c.Get("userID")
	if !ok {
		log.Error(c, "error getting user id from context")
		return
	}

	bookingID, err := strconv.ParseInt(c.Param("id"), 10, 64)
	if err != nil {
		log.Error(c, "error parsing bookingId ", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	err = b.bookingService.ApproveBooking(c, userID.(int64), bookingID)
	if err != nil {
		log.Error(c, "error approving booking ", err)
		if err.Error() == constant.ErrForbiddenApprovedBooking {
			apihelper.AbortErrorHandle(c, common.GeneralForbidden)
			return
		}
		apihelper.AbortErrorHandle(c, common.GeneralServiceUnavailable)
		return
	}

	apihelper.SuccessfulHandle(c, true)
}

func (b *BookingController) RejectBooking(c *gin.Context) {
	userID, ok := c.Get("userID")
	if !ok {
		log.Error(c, "error getting user id from context")
		return
	}

	bookingID, err := strconv.ParseInt(c.Param("id"), 10, 64)
	if err != nil {
		log.Error(c, "error parsing bookingId ", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	err = b.bookingService.RejectBooking(c, userID.(int64), bookingID)
	if err != nil {
		log.Error(c, "error rejecting booking ", err)
		if err.Error() == constant.ErrForbiddenRejectBooking {
			apihelper.AbortErrorHandle(c, common.GeneralForbidden)
			return
		}
		apihelper.AbortErrorHandle(c, common.GeneralServiceUnavailable)
		return
	}

	apihelper.SuccessfulHandle(c, true)
}

func (b *BookingController) CancelBooking(c *gin.Context) {
	userID, ok := c.Get("userID")
	if !ok {
		log.Error(c, "error getting user id from context")
		return
	}

	bookingID, err := strconv.ParseInt(c.Param("id"), 10, 64)
	if err != nil {
		log.Error(c, "error parsing bookingId ", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	err = b.bookingService.CancelBooking(c, userID.(int64), bookingID)
	if err != nil {
		log.Error(c, "error cancelling booking ", err)
		if err.Error() == constant.ErrForbiddenCancelBooking {
			apihelper.AbortErrorHandle(c, common.GeneralForbidden)
			return
		}
		if err.Error() == constant.ErrCancelBookingFailed {
			apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
			return
		}
		apihelper.AbortErrorHandle(c, common.GeneralServiceUnavailable)
		return
	}

	apihelper.SuccessfulHandle(c, true)
}

func (b *BookingController) CheckInBooking(c *gin.Context) {
	userID, ok := c.Get("userID")
	if !ok {
		log.Error(c, "error getting user id from context")
		apihelper.AbortErrorHandle(c, common.GeneralUnauthorized)
		return
	}

	bookingID, err := strconv.ParseInt(c.Param("id"), 10, 64)
	if err != nil {
		log.Error(c, "error parsing bookingId ", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	var req request.CheckInBookingRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		log.Error(c, "error binding request ", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	req.BookingID = bookingID

	checkInResponse, err := b.bookingService.CheckInBooking(c, userID.(int64), &req)
	if err != nil {
		log.Error(c, "error checking in booking ", err)
		if err.Error() == constant.ErrForbiddenCheckInBooking {
			apihelper.AbortErrorHandle(c, common.GeneralForbidden)
			return
		}
		apihelper.AbortErrorHandle(c, common.GeneralServiceUnavailable)
		return
	}

	apihelper.SuccessfulHandle(c, checkInResponse)
}

func NewBookingController(bookingService service.IBookingService) *BookingController {
	return &BookingController{
		bookingService: bookingService,
	}
}
