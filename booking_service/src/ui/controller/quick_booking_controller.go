package controller

import (
	"booking_service/core/domain/common"
	"booking_service/core/domain/dto/request"
	"booking_service/core/domain/dto/response"
	"booking_service/core/service"
	"booking_service/kernel/apihelper"
	"booking_service/kernel/utils"
	"github.com/gin-gonic/gin"
	"github.com/golibs-starter/golib/log"
	"strconv"
)

type QuickBookingController struct {
	quickBookingService service.IQuickBookingService
}

func NewQuickBookingController(quickBookingService service.IQuickBookingService) *QuickBookingController {
	return &QuickBookingController{
		quickBookingService: quickBookingService,
	}
}

func (q *QuickBookingController) CreateQuickBooking(c *gin.Context) {
	userID, ok := c.Get("userID")
	if !ok {
		log.Error(c, "error getting user id from context")
		return
	}

	var req request.CreateQuickBookingDto
	if err := c.ShouldBindJSON(&req); err != nil {
		log.Error(c, "error binding request", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	quickBooking, err := q.quickBookingService.CreateQuickBooking(c, userID.(int64), &req)
	if err != nil {
		log.Error(c, "error creating quick booking", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	apihelper.SuccessfulHandle(c, quickBooking)
}

func (q *QuickBookingController) UpdateQuickBooking(c *gin.Context) {
	userID, ok := c.Get("userID")
	if !ok {
		log.Error(c, "error getting user id from context")
		apihelper.AbortErrorHandle(c, common.GeneralUnauthorized)
		return
	}

	id, err := strconv.ParseInt(c.Param("id"), 10, 64)
	if err != nil {
		log.Error(c, "error parsing quick booking id", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	var req request.UpdateQuickBookingDto
	if err := c.ShouldBindJSON(&req); err != nil {
		log.Error(c, "error binding request", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	quickBooking, err := q.quickBookingService.UpdateQuickBooking(c, userID.(int64), id, &req)
	if err != nil {
		log.Error(c, "error updating quick booking", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	apihelper.SuccessfulHandle(c, quickBooking)
}

func (q *QuickBookingController) DeleteQuickBooking(c *gin.Context) {
	userID, ok := c.Get("userID")
	if !ok {
		log.Error(c, "error getting user id from context")
		apihelper.AbortErrorHandle(c, common.GeneralUnauthorized)
		return
	}

	id, err := strconv.ParseInt(c.Param("id"), 10, 64)
	if err != nil {
		log.Error(c, "error parsing quick booking id", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	err = q.quickBookingService.DeleteQuickBooking(c, userID.(int64), id)
	if err != nil {
		log.Error(c, "error deleting quick booking", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	apihelper.SuccessfulHandle(c, true)
}

func (q *QuickBookingController) GetQuickBooking(c *gin.Context) {
	userID, ok := c.Get("userID")
	if !ok {
		log.Error(c, "error getting user id from context")
		apihelper.AbortErrorHandle(c, common.GeneralUnauthorized)
		return
	}

	id, err := strconv.ParseInt(c.Param("id"), 10, 64)
	if err != nil {
		log.Error(c, "error parsing quick booking id", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	quickBooking, err := q.quickBookingService.GetQuickBooking(c, userID.(int64), id)
	if err != nil {
		log.Error(c, "error getting quick booking", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	apihelper.SuccessfulHandle(c, quickBooking)
}

func (q *QuickBookingController) GetQuickBookings(c *gin.Context) {
	userID, ok := c.Get("userID")
	if !ok {
		log.Error(c, "error getting user id from context")
		apihelper.AbortErrorHandle(c, common.GeneralUnauthorized)
		return
	}

	var params request.QuickBookingParams
	pageSize, page := utils.GetPagingParams(c)
	params.Page = &page
	params.PageSize = &pageSize

	accommodationID, err := utils.ParseIntParam(c, "accommodationId", 64)
	if err != nil {
		log.Error(c, "error parsing accommodation id", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}
	params.AccommodationID = accommodationID

	quickBookings, _, err := q.quickBookingService.GetQuickBookings(c, userID.(int64), &params)
	if err != nil {
		log.Error(c, "error getting quick bookings", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	apihelper.SuccessfulHandle(c, quickBookings)
}

func (q *QuickBookingController) OneClickBooking(c *gin.Context) {
	userID, ok := c.Get("userID")
	if !ok {
		log.Error(c, "error getting user id from context")
		apihelper.AbortErrorHandle(c, common.GeneralUnauthorized)
		return
	}

	var req request.OneClickBookingDto
	if err := c.ShouldBindJSON(&req); err != nil {
		log.Error(c, "error binding request", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	booking, err := q.quickBookingService.OneClickBooking(c, userID.(int64), &req)
	if err != nil {
		log.Error(c, "error creating booking with one-click", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	apihelper.SuccessfulHandle(c, response.ToBookingResponse(booking))
}
