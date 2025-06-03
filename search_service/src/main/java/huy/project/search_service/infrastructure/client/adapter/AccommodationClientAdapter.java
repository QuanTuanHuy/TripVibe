package huy.project.search_service.infrastructure.client.adapter;

import huy.project.search_service.core.domain.constant.ErrorCode;
import huy.project.search_service.core.domain.dto.request.AccommodationThumbnailParams;
import huy.project.search_service.core.domain.dto.response.AccommodationThumbnail;
import huy.project.search_service.core.domain.exception.AppException;
import huy.project.search_service.core.port.client.IAccommodationClientPort;
import huy.project.search_service.infrastructure.client.IAccommodationClient;
import huy.project.search_service.ui.resource.Resource;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccommodationClientAdapter implements IAccommodationClientPort {
    IAccommodationClient accommodationClient;

    private static final String SERVICE_NAME = "accommodationService";

    @Override
    @Retry(name = SERVICE_NAME, fallbackMethod = "fallbackGetAccommodations")
    @CircuitBreaker(name = SERVICE_NAME, fallbackMethod = "fallbackGetAccommodations")
    public List<AccommodationThumbnail> getAccommodations(AccommodationThumbnailParams params) {
        try {
            Resource<List<AccommodationThumbnail>> response = accommodationClient.getAccommodationsThumbnail(
                    params.getIds(),
                    params.getStartDate(),
                    params.getEndDate(),
                    params.getGuestCount()
            );
            if (response == null || response.getMeta() == null) {
                throw new RuntimeException("Invalid response from accommodation service");
            }

            if (response.getMeta().getMessage().equals("Success")) {
                return response.getData() != null ? response.getData() : Collections.emptyList();
            } else {
                throw new AppException(ErrorCode.GET_ACCOMMODATION_FAILED);
            }
        } catch (AppException e) {
            // Non-retry
            throw e;
        } catch (Exception e) {
            log.error("Error while calling accommodation service: {}", e.getMessage());
            throw new RuntimeException("Service call failed", e);
        }
    }

    private List<AccommodationThumbnail> fallbackGetAccommodations(AccommodationThumbnailParams params, Exception e) {
        log.info("Fallback triggered: {}", e.getMessage());
        return Collections.emptyList();
    }
}
