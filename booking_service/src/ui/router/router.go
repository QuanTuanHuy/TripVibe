package router

import (
	"booking_service/core/domain/constant"
	"booking_service/ui/controller"
	"booking_service/ui/middleware"

	"github.com/gin-gonic/gin"
	"github.com/golibs-starter/golib"
	"go.uber.org/fx"
)

type RegisterRoutersIn struct {
	fx.In
	App                       *golib.App
	Engine                    *gin.Engine
	JWTConfig                 *middleware.JWTConfig
	AccommodationController   *controller.AccommodationController
	BookingController         *controller.BookingController
	InternalBookingController *controller.InternalBookingController
	QuickBookingController    *controller.QuickBookingController
}

func RegisterGinRouters(p RegisterRoutersIn) {
	router := p.Engine.Group(p.App.Path())

	accommodationV1 := router.Group("/public/v1/accommodations")
	{
		accommodationV1.GET("/:id", p.AccommodationController.GetAccommodationDetail)
	}
	accommodationV1.Use(middleware.JWTAuthMiddleware(p.JWTConfig), middleware.RoleAuthorization(constant.ROLE_ADMIN))
	{
		accommodationV1.POST("", p.AccommodationController.CreateAccommodation)
		accommodationV1.DELETE("/:id", p.AccommodationController.DeleteAccommodationByID)
		accommodationV1.PUT("", p.AccommodationController.UpdateAccommodation)
	}

	bookingV1 := router.Group("/public/v1/bookings", middleware.JWTAuthMiddleware(p.JWTConfig))
	{
		bookingV1.POST("", p.BookingController.CreateBooking)
		bookingV1.GET("/:id", p.BookingController.GetDetailBooking)
		bookingV1.PUT("/:id/cancel", p.BookingController.CancelBooking)
		bookingV1.GET("", p.BookingController.GetAllBookings)
	}
	bookingV1.Use(middleware.RoleAuthorization(constant.ROLE_OWNER, constant.ROLE_ADMIN))
	{
		bookingV1.PUT("/:id/approve", p.BookingController.ApproveBooking)
		bookingV1.PUT("/:id/reject", p.BookingController.RejectBooking)
	}

	internalV1 := router.Group("/internal/v1")
	internalV1.Use(middleware.JWTAuthMiddleware(p.JWTConfig))
	{
		internalBookings := internalV1.Group("/bookings")
		{
			internalBookings.GET("/find", p.InternalBookingController.GetCompletedBookingByUserIdAndUnitId)
		}
	}

	quickBookingV1 := router.Group("/public/v1/quick_bookings", middleware.JWTAuthMiddleware(p.JWTConfig))
	{
		quickBookingV1.GET("", p.QuickBookingController.GetQuickBookings)
		quickBookingV1.GET("/:id", p.QuickBookingController.GetQuickBooking)
		quickBookingV1.POST("", p.QuickBookingController.CreateQuickBooking)
		quickBookingV1.PUT("/:id", p.QuickBookingController.UpdateQuickBooking)
		quickBookingV1.DELETE("/:id", p.QuickBookingController.DeleteQuickBooking)
	}
}
