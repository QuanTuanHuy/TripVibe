package huy.project.rating_service.core.service;

import huy.project.rating_service.core.domain.dto.response.BookingDetailsDto;

public interface IBookingService {
    BookingDetailsDto getBookingById(Long id);
}
