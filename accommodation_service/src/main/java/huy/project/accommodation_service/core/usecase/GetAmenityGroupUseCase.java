package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.domain.entity.AmenityEntity;
import huy.project.accommodation_service.core.domain.entity.AmenityGroupEntity;
import huy.project.accommodation_service.core.exception.AppException;
import huy.project.accommodation_service.core.port.IAmenityGroupPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetAmenityGroupUseCase {
    private final IAmenityGroupPort amenityGroupPort;

    private final GetAmenityUseCase getAmenityUseCase;

    public AmenityGroupEntity getAmenityGroupById(Long id) {
        AmenityGroupEntity amenityGroup = amenityGroupPort.getAmenityGroupById(id);
        if (amenityGroup == null) {
            log.error("Amenity group not found, id: {}", id);
            throw new AppException(ErrorCode.AMENITY_GROUP_NOT_FOUND);
        }

        amenityGroup.setAmenities(getAmenityUseCase.getAmenitiesByGroupId(id));
        return amenityGroup;
    }

    public List<AmenityGroupEntity> getAllAmenityGroups() {
        List<AmenityGroupEntity> amenityGroups = amenityGroupPort.getAllAmenityGroups();

        List<Long> amenityGroupIds = amenityGroups.stream().map(AmenityGroupEntity::getId).toList();
        List<AmenityEntity> amenities = getAmenityUseCase.getAmenitiesByGroupIds(amenityGroupIds);
        var amenityGroupBy = amenities.stream().collect(Collectors.groupingBy(AmenityEntity::getGroupId));

        amenityGroups.forEach(group -> group.setAmenities(amenityGroupBy.get(group.getId())));
        return amenityGroups;
    }
}
