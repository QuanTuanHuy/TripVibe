package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.domain.entity.AmenityEntity;
import huy.project.accommodation_service.core.domain.entity.CreateAmenityRequestDto;
import huy.project.accommodation_service.core.domain.mapper.AmenityMapper;
import huy.project.accommodation_service.core.exception.AppException;
import huy.project.accommodation_service.core.port.IAmenityPort;
import huy.project.accommodation_service.core.port.ICachePort;
import huy.project.accommodation_service.core.validation.AmenityValidation;
import huy.project.accommodation_service.kernel.utils.CacheUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateAmenityUseCase {
    private final IAmenityPort amenityPort;
    private final ICachePort cachePort;

    private final AmenityValidation amenityValidation;

    @Transactional(rollbackFor = Exception.class)
    public AmenityEntity createAmenity(CreateAmenityRequestDto req) {
        Pair<Boolean, ErrorCode> validationResult = amenityValidation.validateCreateAmenityRequest(req);
        if (!validationResult.getFirst()) {
            log.error("Create amenity failed: {}", validationResult.getSecond());
            throw new AppException(validationResult.getSecond());
        }

        AmenityEntity amenity = AmenityMapper.INSTANCE.toEntity(req);
        amenity = amenityPort.save(amenity);

        // clear cache
        cachePort.deleteFromCache(CacheUtils.buildCacheKeyGetAmenityGroupById(amenity.getGroupId()));
        cachePort.deleteFromCache(CacheUtils.CACHE_AMENITY_GROUP_LIST);

        return amenity;
    }
}
