package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.entity.AccommodationAmenityEntity;
import huy.project.accommodation_service.core.port.IAccommodationAmenityPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAccommodationAmenityUseCase {
    private final IAccommodationAmenityPort accAmenityPort;

    public List<AccommodationAmenityEntity> getAccAmenitiesByAccId(Long accommodationId) {
        return accAmenityPort.getAccAmenitiesByAccId(accommodationId);
    }
}
