package huy.project.accommodation_service.core.port;

import com.nimbusds.jose.util.Pair;
import huy.project.accommodation_service.core.domain.dto.request.AmenityGroupParams;
import huy.project.accommodation_service.core.domain.dto.response.PageInfo;
import huy.project.accommodation_service.core.domain.entity.AmenityGroupEntity;

import java.util.List;

public interface IAmenityGroupPort {
    AmenityGroupEntity save(AmenityGroupEntity amenityGroup);
    AmenityGroupEntity getAmenityGroupById(Long id);
    AmenityGroupEntity getAmenityGroupByName(String name);
    Pair<PageInfo, List<AmenityGroupEntity>> getAllAmenityGroups(AmenityGroupParams params);
    void deleteAmenityGroupById(Long id);
}
