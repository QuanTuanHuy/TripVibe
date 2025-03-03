package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.entity.LocationEntity;
import huy.project.accommodation_service.core.port.ILocationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateLocationUseCase {
    private final ILocationPort locationPort;

    @Transactional(rollbackFor = Exception.class)
    public LocationEntity createLocation(LocationEntity location) {
        return locationPort.save(location);
    }
}
