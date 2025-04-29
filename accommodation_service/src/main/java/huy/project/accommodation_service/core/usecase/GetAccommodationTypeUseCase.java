package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.dto.request.AccommodationTypeParams;
import huy.project.accommodation_service.core.domain.entity.AccommodationTypeEntity;
import huy.project.accommodation_service.core.port.IAccommodationTypePort;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GetAccommodationTypeUseCase {
    IAccommodationTypePort accommodationTypePort;

    public AccommodationTypeEntity getAccommodationTypeById(Long id) {
        return accommodationTypePort.getAccommodationTypeById(id);
    }

    public List<AccommodationTypeEntity> getAccommodationTypes(AccommodationTypeParams params) {
        return accommodationTypePort.getAccommodationTypes(params);
    }
}
