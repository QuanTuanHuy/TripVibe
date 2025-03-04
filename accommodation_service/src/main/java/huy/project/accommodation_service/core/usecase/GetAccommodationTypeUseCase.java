package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.entity.AccommodationTypeEntity;
import huy.project.accommodation_service.core.port.IAccommodationTypePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAccommodationTypeUseCase {
    private final IAccommodationTypePort accommodationTypePort;

    public AccommodationTypeEntity getAccommodationTypeById(Long id) {
        return accommodationTypePort.getAccommodationTypeById(id);
    }
}
