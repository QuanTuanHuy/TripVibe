package huy.project.rating_service.infrastructure.client.adapter;

import huy.project.rating_service.core.domain.constant.ErrorCode;
import huy.project.rating_service.core.domain.dto.BookingDto;
import huy.project.rating_service.core.domain.exception.exception.AppException;
import huy.project.rating_service.core.port.IBookingPort;
import huy.project.rating_service.infrastructure.client.IBookingClient;
import huy.project.rating_service.ui.resource.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingClientAdapter implements IBookingPort {
    private final IBookingClient bookingClient;

    public static final String SUCCESS = "Success";

    @Override
    public BookingDto getCompletedBookingByUserIdAndUnitId(Long userId, Long unitId) {
        try {
            Resource<BookingDto> response = bookingClient.getCompletedBookingByUserIdAndUnitId(userId, unitId);
            if (response.getMeta().getMessage().equals(SUCCESS) && response.getData() != null) {
                return response.getData();
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("Error when calling booking service ", e);
            throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
        }
    }
}
