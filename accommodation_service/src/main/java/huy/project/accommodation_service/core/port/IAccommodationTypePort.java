package huy.project.accommodation_service.core.port;

import huy.project.accommodation_service.core.domain.entity.AccommodationTypeEntity;

public interface IAccommodationTypePort {
    AccommodationTypeEntity save(AccommodationTypeEntity accommodationType);
    AccommodationTypeEntity getAccommodationTypeByName(String name);
    AccommodationTypeEntity getAccommodationTypeById(Long id);
}
