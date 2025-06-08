package huy.project.accommodation_service.core.port;

import huy.project.accommodation_service.core.domain.dto.request.AmenityGroupParams;
import huy.project.accommodation_service.core.domain.dto.response.PageInfo;
import huy.project.accommodation_service.core.domain.entity.AmenityGroupEntity;
import org.springframework.data.util.Pair;

import java.util.List;

public interface IAmenityGroupPort {
    AmenityGroupEntity save(AmenityGroupEntity amenityGroup);
    AmenityGroupEntity getAmenityGroupById(Long id);
    AmenityGroupEntity getAmenityGroupByName(String name);
    Pair<PageInfo, List<AmenityGroupEntity>> getAllAmenityGroups(AmenityGroupParams params);
    void deleteAmenityGroupById(Long id);
    long countAll();
}
