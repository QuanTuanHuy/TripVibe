package huy.project.rating_service.infrastructure.client.adapter;

import huy.project.rating_service.core.domain.constant.ErrorCode;
import huy.project.rating_service.core.domain.dto.request.LoginRequest;
import huy.project.rating_service.core.domain.dto.response.BookingDetailsDto;
import huy.project.rating_service.core.domain.dto.response.BookingDto;
import huy.project.rating_service.core.domain.dto.response.LoginResponse;
import huy.project.rating_service.core.domain.exception.AppException;
import huy.project.rating_service.core.port.IBookingPort;
import huy.project.rating_service.infrastructure.client.IAuthClient;
import huy.project.rating_service.infrastructure.client.IBookingClient;
import huy.project.rating_service.kernel.property.TripvibeProperty;
import huy.project.rating_service.ui.resource.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingClientAdapter implements IBookingPort {
    private final IBookingClient bookingClient;
    private final IAuthClient authClient;
    private final TripvibeProperty authProperty;

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

    @Override
    public BookingDetailsDto getBookingById(Long id) {
        LoginRequest loginRequest = LoginRequest.builder()
                .email(authProperty.getEmail())
                .password(authProperty.getPassword())
                .build();
        String token;
        try {
            Resource<LoginResponse> loginResponse = authClient.login(loginRequest);
            if (loginResponse.getMeta().getCode() == 200) {
                token = loginResponse.getData().getAccessToken();
            } else {
                log.error("Error when calling auth service: {}", loginResponse.getMeta().getMessage());
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.error("Error when calling auth service ", e);
            throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
        }

        token = "Bearer " + token;
        try {
            Resource<BookingDetailsDto> response = bookingClient.getBookingById(id, token);
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
