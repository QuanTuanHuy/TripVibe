package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.domain.dto.request.UpdateAmenityGroupRequestDto;
import huy.project.accommodation_service.core.domain.entity.AmenityGroupEntity;
import huy.project.accommodation_service.core.domain.mapper.AmenityGroupMapper;
import huy.project.accommodation_service.core.exception.AppException;
import huy.project.accommodation_service.core.port.IAmenityGroupPort;
import huy.project.accommodation_service.core.port.ICachePort;
import huy.project.accommodation_service.core.validation.AmenityGroupValidation;
import huy.project.accommodation_service.kernel.utils.CacheUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateAmenityGroupUseCase {
    private final IAmenityGroupPort amenityGroupPort;
    private final ICachePort cachePort;

    private final AmenityGroupValidation amenityGroupValidation;

    @Transactional(rollbackFor = Exception.class)
    public AmenityGroupEntity updateAmenityGroup(Long id, UpdateAmenityGroupRequestDto req) {
        AmenityGroupEntity existedAmenityGroup = amenityGroupPort.getAmenityGroupById(id);
        if (existedAmenityGroup == null) {
            log.error("Amenity group not found with id: {}", id);
            throw new AppException(ErrorCode.AMENITY_GROUP_NOT_FOUND);
        }

        Pair<Boolean, ErrorCode> validationResult = amenityGroupValidation
                .validateUpdateAmenityGroupRequest(existedAmenityGroup, req);
        if (!validationResult.getFirst()) {
            log.error("Update amenity group failed, name is existed, {}", req.getName());
            throw new AppException(validationResult.getSecond());
        }

        AmenityGroupEntity newAmenityGroup = AmenityGroupMapper.INSTANCE.toEntity(existedAmenityGroup, req);
        newAmenityGroup = amenityGroupPort.save(newAmenityGroup);

        // clear cache
        cachePort.deleteFromCache(CacheUtils.CACHE_AMENITY_GROUP_LIST);
        cachePort.deleteFromCache(CacheUtils.buildCacheKeyGetAmenityGroupById(id));

        return newAmenityGroup;
    }
}
