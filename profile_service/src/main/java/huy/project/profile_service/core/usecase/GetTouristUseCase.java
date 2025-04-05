package huy.project.profile_service.core.usecase;

import huy.project.profile_service.core.domain.constant.CacheConstant;
import huy.project.profile_service.core.domain.constant.ErrorCode;
import huy.project.profile_service.core.domain.entity.LocationEntity;
import huy.project.profile_service.core.domain.entity.TouristEntity;
import huy.project.profile_service.core.domain.exception.AppException;
import huy.project.profile_service.core.port.*;
import huy.project.profile_service.kernel.utils.CacheUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetTouristUseCase {
    private final ITouristPort touristPort;
    private final ILocationPort locationPort;
    private final IPassportPort passportPort;
    private final ICreditCardPort creditCardPort;
    private final IUserSettingPort userSettingPort;
    private final ICachePort cachePort;

    public TouristEntity getTouristById(Long id) {
        var tourist = touristPort.getTouristById(id);
        if (tourist == null) {
            log.error("getTouristById: tourist not found, id: {}", id);
            throw new AppException(ErrorCode.TOURIST_NOT_FOUND);
        }
        return tourist;
    }

    public TouristEntity getDetailTourist(Long id) {
        // get from cache
        String key = CacheUtils.buildCacheKeyGetTouristById(id);
        var cachedTourist = cachePort.getFromCache(key, TouristEntity.class);
        if (cachedTourist != null) {
            return cachedTourist;
        }

        // get from db
        var tourist = getTouristById(id);
        if (tourist.getLocationId() != null) {
            tourist.setLocation(locationPort.getLocationById(tourist.getLocationId()));
        }
        if (tourist.getPassportId() != null) {
            tourist.setPassport(passportPort.getPassportById(tourist.getPassportId()));
        }
        if (tourist.getCreditCardId() != null) {
            tourist.setCreditCard(creditCardPort.getCreditCardById(tourist.getCreditCardId()));
        }
        if (tourist.getUserSettingId() != null) {
            tourist.setUserSetting(userSettingPort.getUserSettingById(tourist.getUserSettingId()));
        }

        // set to cache
        cachePort.setToCache(key, tourist, CacheConstant.DEFAULT_TTL);

        return tourist;
    }

    public List<TouristEntity> getTouristsByIds(List<Long> touristIds) {
        var tourists = touristPort.getTouristsByIds(touristIds);
        if (CollectionUtils.isEmpty(tourists)) {
            return tourists;
        }

        List<Long> locationIds = tourists.stream().map(TouristEntity::getLocationId).toList();
        List<LocationEntity> locations = locationPort.getLocationsByIds(locationIds);
        var locationMap = locations.stream()
                .collect(Collectors.toMap(LocationEntity::getId, Function.identity()));

        return tourists.stream()
                .peek(tourist -> tourist.setLocation(locationMap.get(tourist.getLocationId())))
                .toList();
    }
}
