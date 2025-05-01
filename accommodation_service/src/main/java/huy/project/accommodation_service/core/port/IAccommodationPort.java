package huy.project.accommodation_service.core.port;

import huy.project.accommodation_service.core.domain.dto.request.AccommodationParams;
import huy.project.accommodation_service.core.domain.entity.AccommodationEntity;

import java.util.List;

public interface IAccommodationPort {
    AccommodationEntity save(AccommodationEntity accommodation);
    AccommodationEntity getAccommodationByName(String name);
    AccommodationEntity getAccommodationById(Long id);
    List<AccommodationEntity> getAccommodations(AccommodationParams params);
    void deleteAccommodationById(Long id);

    List<AccommodationEntity> getAccommodationsByIds(List<Long> ids);
}
