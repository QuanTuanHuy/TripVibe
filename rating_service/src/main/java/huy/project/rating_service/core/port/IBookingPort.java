package huy.project.rating_service.core.port;

import huy.project.rating_service.core.domain.dto.response.BookingDetailsDto;
import huy.project.rating_service.core.domain.dto.response.BookingDto;

public interface IBookingPort {
    BookingDto getCompletedBookingByUserIdAndUnitId(Long userId, Long unitId);
    BookingDetailsDto getBookingById(Long id);
}
