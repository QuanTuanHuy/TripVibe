package huy.project.search_service.core.service;

import huy.project.search_service.core.domain.entity.AccommodationEntity;

public interface IAccommodationService {
    AccommodationEntity createAccommodation(AccommodationEntity accommodation);
    AccommodationEntity getAccById(Long id);
    void deleteAccommodation(Long id);
}
