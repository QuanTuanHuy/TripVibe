package controller

import (
	"booking_service/core/domain/common"
	"booking_service/core/domain/constant"
	"booking_service/core/domain/dto/request"
	"booking_service/core/domain/dto/response"
	"booking_service/core/service"
	"booking_service/kernel/apihelper"
	"booking_service/kernel/utils"
	"strconv"

	"github.com/gin-gonic/gin"
	"github.com/golibs-starter/golib/log"
)

type BookingController struct {
	bookingService service.IBookingService
}

func (b *BookingController) GetAllBookings(c *gin.Context) {
	var params request.BookingParams
	pageSize, page := utils.GetPagingParams(c)
	params.Page = &page
	params.PageSize = &pageSize

	userID, err := utils.ParseIntParam(c, "userId", 64)
	if err != nil {
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}
	params.UserID = userID

	accommodationID, err := utils.ParseIntParam(c, "accommodationId", 64)
	if err != nil {
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}
	params.AccommodationID = accommodationID

	unitID, err := utils.ParseIntParam(c, "unitId", 64)
	if err != nil {
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}
	params.UnitID = unitID

	startTime, err := utils.ParseIntParam(c, "startTime", 64)
	if err != nil {
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}
	params.StartTime = startTime

	endTime, err := utils.ParseIntParam(c, "endTime", 64)
	if err != nil {
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}
	params.EndTime = endTime
	if status := c.Query("status"); status != "" {
		params.Status = &status
	}

	if dateType := c.Query("dateType"); dateType != "" {
		params.DateType = &dateType
	} else {
		dateType := "stay_from"
		params.DateType = &dateType
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
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	apihelper.SuccessfulHandle(c, response.ToBookingResponse(booking))
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

func NewBookingController(bookingService service.IBookingService) *BookingController {
	return &BookingController{
		bookingService: bookingService,
	}
}
