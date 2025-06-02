package huy.project.accommodation_service.core.service;

import huy.project.accommodation_service.core.domain.dto.request.UpdateAmenityRequestDto;
import huy.project.accommodation_service.core.domain.entity.AmenityEntity;
import huy.project.accommodation_service.core.domain.dto.request.CreateAmenityRequestDto;

import java.util.List;

public interface IAmenityService {
    AmenityEntity createAmenity(CreateAmenityRequestDto req);
    AmenityEntity updateAmenity(Long id, UpdateAmenityRequestDto req);
    void deleteAmenity(Long id);
    void createIfNotExists(List<CreateAmenityRequestDto> amenities);
}
