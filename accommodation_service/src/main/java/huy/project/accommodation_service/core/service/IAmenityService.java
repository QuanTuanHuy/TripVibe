package huy.project.accommodation_service.core.service;

import huy.project.accommodation_service.core.domain.entity.AmenityEntity;
import huy.project.accommodation_service.core.domain.entity.CreateAmenityRequestDto;

public interface IAmenityService {
    AmenityEntity createAmenity(CreateAmenityRequestDto req);
}
