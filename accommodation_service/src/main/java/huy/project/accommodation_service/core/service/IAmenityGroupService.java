package huy.project.accommodation_service.core.service;

import huy.project.accommodation_service.core.domain.dto.request.CreateAmenityGroupRequestDto;
import huy.project.accommodation_service.core.domain.entity.AmenityGroupEntity;

public interface IAmenityGroupService {
    AmenityGroupEntity createAmenityGroup(CreateAmenityGroupRequestDto req);
}
