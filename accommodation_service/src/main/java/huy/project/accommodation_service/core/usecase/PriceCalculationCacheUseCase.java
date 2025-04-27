package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.constant.CacheConstant;
import huy.project.accommodation_service.core.domain.dto.request.PriceCalculationRequest;
import huy.project.accommodation_service.core.domain.dto.response.PriceCalculationResponse;
import huy.project.accommodation_service.core.port.ICachePort;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PriceCalculationCacheUseCase {
    ICachePort cachePort;

    public String buildPriceCalculationCacheKey(PriceCalculationRequest request) {
        return String.format("price_calculation:unit_%d:from_%s:to_%s:guests_%d",
                request.getUnitId(),
                request.getCheckInDate(),
                request.getCheckOutDate(),
                request.getGuestCount());
    }

    public PriceCalculationResponse getFromCache(PriceCalculationRequest request) {
        try {
            var key = buildPriceCalculationCacheKey(request);
            var cachedResponse = cachePort.getFromCache(key, PriceCalculationResponse.class);

            if (cachedResponse != null) {
                log.info("Cache hit for key: {}", key);
                return cachedResponse;
            } else {
                log.info("Cache miss for key: {}", key);
                return null;
            }
        } catch (Exception e) {
            log.error("Error while getting from cache: {}", e.getMessage());
            return null;
        }
    }

    public void saveToCache(PriceCalculationRequest request, PriceCalculationResponse response) {
        try {
            var key = buildPriceCalculationCacheKey(request);
            cachePort.setToCache(key, response, CacheConstant.DEFAULT_TTL);
            log.info("Saved to cache with key: {}", key);
        } catch (Exception e) {
            log.error("Error while saving to cache: {}", e.getMessage());
        }
    }

    public boolean invalidatePriceCache(Long unitId) {
        try {
            String keyPattern = String.format("price_calculation:unit_%d:*", unitId);
            cachePort.deleteKeysByPattern(keyPattern);
            log.debug("Invalidated cache for unitId: {}", unitId);
            return true;
        } catch (Exception e) {
            log.error("Error while invalidating cache for unitId {}: {}", unitId, e.getMessage());
            return false;
        }
    }
}
