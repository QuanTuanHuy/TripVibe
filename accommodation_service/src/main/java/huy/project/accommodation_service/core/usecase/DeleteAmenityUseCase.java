package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.domain.entity.AmenityEntity;
import huy.project.accommodation_service.core.exception.AppException;
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
public class DeleteAmenityUseCase {
    private final IAmenityPort amenityPort;
    private final ICachePort cachePort;

    @Transactional(rollbackFor = Exception.class)
    public void deleteAmenityById(Long id) {
        AmenityEntity existedAmenity = amenityPort.getAmenityById(id);
        if (existedAmenity == null) {
            log.error("Amenity not found with id: {}", id);
            throw new AppException(ErrorCode.AMENITY_NOT_FOUND);
        }

        amenityPort.deleteAmenityById(id);

        // clear cache
        cachePort.deleteFromCache(CacheUtils.buildCacheKeyGetAmenityGroupById(existedAmenity.getGroupId()));
        cachePort.deleteFromCache(CacheUtils.CACHE_AMENITY_GROUP_LIST);
    }
}
