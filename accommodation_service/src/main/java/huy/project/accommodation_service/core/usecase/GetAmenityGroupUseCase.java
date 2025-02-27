package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.constant.CacheConstant;
import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.domain.entity.AmenityEntity;
import huy.project.accommodation_service.core.domain.entity.AmenityGroupEntity;
import huy.project.accommodation_service.core.exception.AppException;
import huy.project.accommodation_service.core.port.IAmenityGroupPort;
import huy.project.accommodation_service.core.port.ICachePort;
import huy.project.accommodation_service.kernel.utils.CacheUtils;
import huy.project.accommodation_service.kernel.utils.JsonUtils;
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
    private final ICachePort cachePort;

    private final GetAmenityUseCase getAmenityUseCase;

    private final JsonUtils jsonUtils;

    public AmenityGroupEntity getAmenityGroupById(Long id) {
        // get from cache
        String key = CacheUtils.buildCacheKeyGetAmenityGroupById(id);
        AmenityGroupEntity cachedAmenityGroup = cachePort.getFromCache(key, AmenityGroupEntity.class);
        if (cachedAmenityGroup != null) {
            return cachedAmenityGroup;
        }

        // get from db
        AmenityGroupEntity amenityGroup = amenityGroupPort.getAmenityGroupById(id);
        if (amenityGroup == null) {
            log.error("Amenity group not found, id: {}", id);
            throw new AppException(ErrorCode.AMENITY_GROUP_NOT_FOUND);
        }

        amenityGroup.setAmenities(getAmenityUseCase.getAmenitiesByGroupId(id));

        // set to cache
        cachePort.setToCache(key, amenityGroup, CacheConstant.DEFAULT_TTL);

        return amenityGroup;
    }

    public List<AmenityGroupEntity> getAllAmenityGroups() {
        // get from cache
        String cachedAmenityGroups = cachePort.getFromCache(CacheUtils.CACHE_PREFIX_AMENITY_GROUP_LIST);
        if (cachedAmenityGroups != null) {
            return jsonUtils.fromJsonList(cachedAmenityGroups, AmenityGroupEntity.class);
        }

        // get from db
        List<AmenityGroupEntity> amenityGroups = amenityGroupPort.getAllAmenityGroups();

        List<Long> amenityGroupIds = amenityGroups.stream().map(AmenityGroupEntity::getId).toList();
        List<AmenityEntity> amenities = getAmenityUseCase.getAmenitiesByGroupIds(amenityGroupIds);
        var amenityGroupBy = amenities.stream().collect(Collectors.groupingBy(AmenityEntity::getGroupId));

        amenityGroups.forEach(group -> group.setAmenities(amenityGroupBy.get(group.getId())));

        // set to cache
        cachePort.setToCache(CacheUtils.CACHE_PREFIX_AMENITY_GROUP_LIST, amenityGroups, CacheConstant.DEFAULT_TTL);

        return amenityGroups;
    }
}
