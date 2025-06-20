package huy.project.rating_service.core.service.impl;

import huy.project.rating_service.core.domain.dto.response.BookingDetailsDto;
import huy.project.rating_service.core.port.IBookingPort;
import huy.project.rating_service.core.service.IBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingService implements IBookingService {
    private final IBookingPort bookingPort;

    @Override
    public BookingDetailsDto getBookingById(Long id) {
        return bookingPort.getBookingById(id);
    }
}
