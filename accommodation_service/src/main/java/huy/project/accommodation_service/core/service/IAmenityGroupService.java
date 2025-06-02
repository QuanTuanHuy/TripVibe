package huy.project.accommodation_service.core.service;

import huy.project.accommodation_service.core.domain.dto.request.AmenityGroupParams;
import huy.project.accommodation_service.core.domain.dto.request.CreateAmenityGroupRequestDto;
import huy.project.accommodation_service.core.domain.dto.request.UpdateAmenityGroupRequestDto;
import huy.project.accommodation_service.core.domain.dto.response.PageInfo;
import huy.project.accommodation_service.core.domain.entity.AmenityGroupEntity;
import org.springframework.data.util.Pair;

import java.util.List;

public interface IAmenityGroupService {
    AmenityGroupEntity createAmenityGroup(CreateAmenityGroupRequestDto req);
    AmenityGroupEntity getAmenityGroupById(Long id);
    Pair<PageInfo, List<AmenityGroupEntity>> getAllAmenityGroups(AmenityGroupParams params);
    void deleteAmenityGroupById(Long id);
    AmenityGroupEntity updateAmenityGroup(Long id, UpdateAmenityGroupRequestDto req);
    void createIfNotExists(List<CreateAmenityGroupRequestDto> amenityGroups);
}
