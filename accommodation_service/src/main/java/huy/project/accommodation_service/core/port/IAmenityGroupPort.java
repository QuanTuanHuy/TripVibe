package huy.project.accommodation_service.core.port;

import huy.project.accommodation_service.core.domain.entity.AmenityGroupEntity;

import java.util.List;

public interface IAmenityGroupPort {
    AmenityGroupEntity save(AmenityGroupEntity amenityGroup);
    AmenityGroupEntity getAmenityGroupById(Long id);
    AmenityGroupEntity getAmenityGroupByName(String name);
    List<AmenityGroupEntity> getAllAmenityGroups();
}
