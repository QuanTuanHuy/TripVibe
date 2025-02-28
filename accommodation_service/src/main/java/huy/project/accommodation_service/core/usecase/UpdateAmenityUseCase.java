package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.domain.dto.request.UpdateAmenityRequestDto;
import huy.project.accommodation_service.core.domain.entity.AmenityEntity;
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
public class UpdateAmenityUseCase {
    private final IAmenityPort amenityPort;
    private final ICachePort cachePort;

    private final AmenityValidation amenityValidation;

    @Transactional(rollbackFor = Exception.class)
    public AmenityEntity updateAmenity(Long id, UpdateAmenityRequestDto req) {
        AmenityEntity existedAmenity = amenityPort.getAmenityById(id);
        if (existedAmenity == null) {
            log.error("Amenity not found with id: {}", id);
            throw new AppException(ErrorCode.AMENITY_NOT_FOUND);
        }

        // validate req
        Pair<Boolean, ErrorCode> validationResult = amenityValidation.validateUpdateAmenityRequest(existedAmenity, req);
        if (!validationResult.getFirst()) {
            log.error("Update amenity request is invalid: {}", validationResult.getSecond());
            throw new AppException(validationResult.getSecond());
        }

        AmenityEntity newAmenity = AmenityMapper.INSTANCE.toEntity(existedAmenity, req);
        newAmenity = amenityPort.save(newAmenity);

        // clear cache
        cachePort.deleteFromCache(CacheUtils.buildCacheKeyGetAmenityGroupById(newAmenity.getGroupId()));
        cachePort.deleteFromCache(CacheUtils.CACHE_AMENITY_GROUP_LIST);

        return newAmenity;
    }
}
