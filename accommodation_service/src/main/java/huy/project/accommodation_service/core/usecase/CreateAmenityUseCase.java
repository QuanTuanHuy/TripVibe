package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.domain.entity.AmenityEntity;
import huy.project.accommodation_service.core.domain.dto.request.CreateAmenityRequestDto;
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

import java.util.List;

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
        if (req.getIsPaid() == null) {
            amenity.setIsPaid(false);
        }
        if (req.getIsHighlighted() == null) {
            amenity.setIsHighlighted(false);
        }
        if (req.getIsFilterable() == null) {
            amenity.setIsFilterable(false);
        }

        amenity = amenityPort.save(amenity);

        // clear cache
        cachePort.deleteFromCache(CacheUtils.buildCacheKeyGetAmenityGroupById(amenity.getGroupId()));
//        cachePort.deleteFromCache(CacheUtils.CACHE_AMENITY_GROUP_LIST);

        return amenity;
    }

    @Transactional(rollbackFor = Exception.class)
    public void createIfNotExists(List<CreateAmenityRequestDto> amenities) {
        if (amenityPort.countAll() > 0) {
            log.info("Amenities already exist, skipping creation.");
            return;
        }

        amenities.forEach(amenity -> {
            try {
                createAmenity(amenity);
            } catch (Exception e) {
                log.warn("Failed to create amenity: {}, error: {}", amenity.getName(), e.getMessage());
            }
        });
    }
}
