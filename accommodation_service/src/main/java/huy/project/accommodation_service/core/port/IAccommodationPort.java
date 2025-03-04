package huy.project.accommodation_service.core.port;

import huy.project.accommodation_service.core.domain.entity.AccommodationEntity;

public interface IAccommodationPort {
    AccommodationEntity save(AccommodationEntity accommodation);
    AccommodationEntity getAccommodationByName(String name);
}
