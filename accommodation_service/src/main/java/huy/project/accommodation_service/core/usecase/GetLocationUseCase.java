package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.domain.entity.LocationEntity;
import huy.project.accommodation_service.core.exception.AppException;
import huy.project.accommodation_service.core.port.ILocationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetLocationUseCase {
    private final ILocationPort locationPort;

    public LocationEntity getLocationById(Long id) {
        LocationEntity location = locationPort.getLocationById(id);
        if (location == null) {
            log.error("Location not found, id: {}", id);
            throw new AppException(ErrorCode.LOCATION_NOT_FOUND);
        }
        return location;
    }
}
