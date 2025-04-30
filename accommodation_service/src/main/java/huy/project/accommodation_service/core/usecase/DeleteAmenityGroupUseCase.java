package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.domain.entity.AmenityGroupEntity;
import huy.project.accommodation_service.core.exception.AppException;
import huy.project.accommodation_service.core.port.IAmenityGroupPort;
import huy.project.accommodation_service.core.port.IAmenityPort;
import huy.project.accommodation_service.core.port.ICachePort;
import huy.project.accommodation_service.kernel.utils.CacheUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteAmenityGroupUseCase {
    private final IAmenityGroupPort amenityGroupPort;
    private final IAmenityPort amenityPort;

    private final ICachePort cachePort;

    @Transactional(rollbackFor = Exception.class)
    public void deleteAmenityGroupById(Long id) {
        AmenityGroupEntity existedAmenity = amenityGroupPort.getAmenityGroupById(id);
        if (existedAmenity == null) {
            log.error("Amenity group not found with id: {}", id);
            throw new AppException(ErrorCode.AMENITY_GROUP_NOT_FOUND);
        }

        amenityPort.deleteAmenityByGroupId(id);
        amenityGroupPort.deleteAmenityGroupById(id);

        // clear cache
//        cachePort.deleteFromCache(CacheUtils.CACHE_AMENITY_GROUP_LIST);
        cachePort.deleteFromCache(CacheUtils.buildCacheKeyGetAmenityGroupById(id));
    }
}
