package huy.project.inventory_service.core.usecase;

import huy.project.inventory_service.core.domain.constant.CacheConstant;
import huy.project.inventory_service.core.domain.entity.Accommodation;
import huy.project.inventory_service.core.domain.exception.NotFoundException;
import huy.project.inventory_service.core.port.IAccommodationPort;
import huy.project.inventory_service.core.port.ICachePort;
import huy.project.inventory_service.kernel.util.CacheUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAccommodationUseCase {
    private final IAccommodationPort accommodationPort;
    private final ICachePort cachePort;
    private final GetUnitUseCase getUnitUseCase;

    public Accommodation getAccommodationById(Long id) {
        return getAccommodationById(id, false);
    }

    public Accommodation getAccommodationById(Long id, boolean includeUnits) {
        var cacheKey = CacheUtils.buildCacheKeyGetAccById(id, includeUnits);
        var cachedAcc = cachePort.getFromCache(cacheKey, Accommodation.class);
        if (cachedAcc != null) {
            return cachedAcc;
        }

        var accommodation = accommodationPort.getAccommodationById(id);
        if (accommodation == null) {
            throw new NotFoundException("Accommodation not found, id: " + id);
        }

        if (includeUnits) {
            accommodation.setUnits(getUnitUseCase.getUnitsByAccommodationId(id, true));
        }

        cachePort.setToCache(cacheKey, accommodation, CacheConstant.DEFAULT_TTL);

        return accommodation;
    }
}
