package huy.project.rating_service.core.usecase;

import huy.project.rating_service.core.domain.constant.CacheConstant;
import huy.project.rating_service.core.domain.dto.response.AccommodationDto;
import huy.project.rating_service.core.domain.dto.response.UnitDto;
import huy.project.rating_service.core.port.IAccommodationPort;
import huy.project.rating_service.core.port.ICachePort;
import huy.project.rating_service.kernel.utils.CacheUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class GetAccommodationUseCase {
    IAccommodationPort accommodationPort;
    ICachePort cachePort;

    public AccommodationDto getAccById(Long id) {
        String cacheKey = CacheUtils.buildCacheKeyGetAccommodationById(id);
        var cachedAcc = cachePort.getFromCache(cacheKey, AccommodationDto.class);
        if (cachedAcc != null) {
            return cachedAcc;
        }

        var accommodation = accommodationPort.getAccById(id);
        if (accommodation == null) {
            return null;
        }

        cachePort.setToCache(cacheKey, accommodation, CacheConstant.DEFAULT_TTL);
        return accommodation;
    }

    public List<UnitDto> getUnitsByIds(List<Long> unitIds) {
        Set<Long> uniqueUnitIds = Set.copyOf(unitIds);
        List<Long> unitIdsCacheMiss = new ArrayList<>();
        List<UnitDto> units = new ArrayList<>();

        uniqueUnitIds.stream()
                .parallel()
                .forEach(unitId -> {
                    String cacheKey = CacheUtils.buildCacheKeyGetUnitById(unitId);
                    var cachedUnit = cachePort.getFromCache(cacheKey, UnitDto.class);
                    if (cachedUnit != null) {
                        units.add(cachedUnit);
                    } else {
                        unitIdsCacheMiss.add(unitId);
                    }
                });

        if (!CollectionUtils.isEmpty(unitIdsCacheMiss)) {
            List<UnitDto> fetchUnits = accommodationPort.getUnitsByIds(unitIdsCacheMiss);
            if (!CollectionUtils.isEmpty(fetchUnits)) {
                fetchUnits.forEach(unit -> {
                    String cacheKey = CacheUtils.buildCacheKeyGetUnitById(unit.getId());
                    cachePort.setToCache(cacheKey, unit, CacheConstant.DEFAULT_TTL);
                });
                units.addAll(fetchUnits);
            }
        }

        return units;
    }
}
