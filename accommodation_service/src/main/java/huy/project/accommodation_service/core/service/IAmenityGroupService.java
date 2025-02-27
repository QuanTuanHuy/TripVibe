package huy.project.accommodation_service.core.service;

import huy.project.accommodation_service.core.domain.dto.request.CreateAmenityGroupRequestDto;
import huy.project.accommodation_service.core.domain.dto.request.UpdateAmenityGroupRequestDto;
import huy.project.accommodation_service.core.domain.entity.AmenityGroupEntity;

import java.util.List;

public interface IAmenityGroupService {
    AmenityGroupEntity createAmenityGroup(CreateAmenityGroupRequestDto req);
    AmenityGroupEntity getAmenityGroupById(Long id);
    List<AmenityGroupEntity> getAllAmenityGroups();
    void deleteAmenityGroupById(Long id);
    AmenityGroupEntity updateAmenityGroup(Long id, UpdateAmenityGroupRequestDto req);
}
