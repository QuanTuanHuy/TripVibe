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
	App                     *golib.App
	Engine                  *gin.Engine
	JWTConfig               *middleware.JWTConfig
	AccommodationController *controller.AccommodationController
	BookingController       *controller.BookingController
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
		bookingV1.GET("", p.BookingController.GetAllBookings)
	}
	bookingV1.Use(middleware.RoleAuthorization(constant.ROLE_OWNER, constant.ROLE_ADMIN))
	{
		bookingV1.PUT("/:id/approve", p.BookingController.ApproveBooking)
		bookingV1.PUT("/:id/reject", p.BookingController.RejectBooking)
	}
}
