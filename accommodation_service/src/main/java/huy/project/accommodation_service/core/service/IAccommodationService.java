package huy.project.accommodation_service.core.service;

import huy.project.accommodation_service.core.domain.dto.request.CreateAccommodationDto;
import huy.project.accommodation_service.core.domain.entity.AccommodationEntity;

public interface IAccommodationService {
    AccommodationEntity createAccommodation(Long userId, CreateAccommodationDto req);
}
