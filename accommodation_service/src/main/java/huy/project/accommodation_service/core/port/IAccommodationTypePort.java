package huy.project.accommodation_service.core.port;

import huy.project.accommodation_service.core.domain.dto.request.AccommodationTypeParams;
import huy.project.accommodation_service.core.domain.entity.AccommodationTypeEntity;

import java.util.List;

public interface IAccommodationTypePort {
    AccommodationTypeEntity save(AccommodationTypeEntity accommodationType);
    AccommodationTypeEntity getAccommodationTypeByName(String name);
    AccommodationTypeEntity getAccommodationTypeById(Long id);

    List<AccommodationTypeEntity> getAccommodationTypes(AccommodationTypeParams params);
    long countAll();
}
