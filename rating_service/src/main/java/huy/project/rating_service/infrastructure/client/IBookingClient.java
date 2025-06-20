package huy.project.rating_service.infrastructure.client;

import huy.project.rating_service.core.domain.dto.response.BookingDetailsDto;
import huy.project.rating_service.core.domain.dto.response.BookingDto;
import huy.project.rating_service.kernel.config.FeignClientConfig;
import huy.project.rating_service.ui.resource.Resource;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "booking-service",
        url = "${app.services.booking_service.url}",
        configuration = FeignClientConfig.class
)
public interface IBookingClient {
    @GetMapping("/api/internal/v1/bookings/find")
    Resource<BookingDto> getCompletedBookingByUserIdAndUnitId(
            @RequestParam("userId") Long userId,
            @RequestParam("unitId") Long unitId
    );

    @GetMapping("/api/internal/v1/bookings/{id}")
    Resource<BookingDetailsDto> getBookingById(
            @PathVariable("id") Long id,
            @RequestHeader("Authorization") String token
    );
}
