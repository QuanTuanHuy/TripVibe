package huy.project.accommodation_service.core.service;

import huy.project.accommodation_service.core.domain.dto.request.CreateAccommodationTypeDto;
import huy.project.accommodation_service.core.domain.entity.AccommodationTypeEntity;

public interface IAccommodationTypeService {
    AccommodationTypeEntity createAccommodationType(CreateAccommodationTypeDto req);
}
