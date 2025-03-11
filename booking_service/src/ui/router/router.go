package router

import (
	"booking_service/ui/controller"
	"github.com/gin-gonic/gin"
	"github.com/golibs-starter/golib"
	"go.uber.org/fx"
)

type RegisterRoutersIn struct {
	fx.In
	App                     *golib.App
	Engine                  *gin.Engine
	AccommodationController *controller.AccommodationController
	BookingController       *controller.BookingController
}

func RegisterGinRouters(p RegisterRoutersIn) {
	router := p.Engine.Group(p.App.Path())
	accommodationV1 := router.Group("public/v1/accommodations")
	{
		accommodationV1.POST("", p.AccommodationController.CreateAccommodation)
		accommodationV1.GET("/:id", p.AccommodationController.GetAccommodationDetail)
		accommodationV1.DELETE("/:id", p.AccommodationController.DeleteAccommodationByID)
		accommodationV1.PUT("", p.AccommodationController.UpdateAccommodation)
	}

	bookingV1 := router.Group("public/v1/bookings")
	{
		bookingV1.POST("", p.BookingController.CreateBooking)
		bookingV1.GET("/:id", p.BookingController.GetDetailBooking)
		bookingV1.GET("", p.BookingController.GetAllBookings)
	}
}
