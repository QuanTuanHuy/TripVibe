package huy.project.profile_service.core.usecase;

import huy.project.profile_service.core.domain.constant.ErrorCode;
import huy.project.profile_service.core.domain.dto.request.CreateCreditCardDto;
import huy.project.profile_service.core.domain.dto.request.UpdateTouristDto;
import huy.project.profile_service.core.domain.entity.*;
import huy.project.profile_service.core.domain.exception.AppException;
import huy.project.profile_service.core.domain.mapper.*;
import huy.project.profile_service.core.port.*;
import huy.project.profile_service.core.port.client.IFileStoragePort;
import huy.project.profile_service.kernel.utils.CacheUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateTouristUseCase {
    private final ITouristPort touristPort;
    private final ILocationPort locationPort;
    private final IPassportPort passportPort;
    private final ICreditCardPort creditCardPort;
    private final IUserSettingPort userSettingPort;
    private final GetTouristUseCase getTouristUseCase;

    private final ICachePort cachePort;
    private final IFileStoragePort fileStoragePort;

    @Transactional(rollbackFor = Exception.class)
    public TouristEntity updateTourist(Long id, UpdateTouristDto req) {
        TouristEntity tourist = touristPort.getTouristById(id);
        if (tourist == null) {
            log.error("Tourist not found with id: {}", id);
            throw new AppException(ErrorCode.TOURIST_NOT_FOUND);
        }

        TouristMapper.INSTANCE.toEntity(tourist, req);

        if (req.getLocation() != null) {
            if (tourist.getLocationId() != null) {
                locationPort.deleteLocationById(tourist.getLocationId());
            }
            LocationEntity location = LocationMapper.INSTANCE.toEntity(req.getLocation());
            location = locationPort.save(location);
            tourist.setLocationId(location.getId());
        }

        if (req.getPassport() != null) {
            if (tourist.getPassportId() != null) {
                passportPort.deletePassportById(tourist.getPassportId());
            }
            PassportEntity passport = PassportMapper.INSTANCE.toEntity(req.getPassport());
            passport = passportPort.save(passport);
            tourist.setPassportId(passport.getId());
        }

        if (req.getUserSetting() != null) {
            if (tourist.getUserSettingId() != null) {
                 userSettingPort.deleteUserSetting(tourist.getUserSettingId());
            }
            UserSettingEntity userSetting = UserSettingMapper.INSTANCE.toEntity(req.getUserSetting());
            userSetting = userSettingPort.save(userSetting);
            tourist.setUserSettingId(userSetting.getId());
        }

        tourist = touristPort.save(tourist);

        // clear cache
        cachePort.deleteFromCache(CacheUtils.buildCacheKeyGetTouristById(tourist.getId()));

        return tourist;
    }

    @Transactional(rollbackFor = Exception.class)
    public CreditCardEntity addCreditCardToTourist(Long touristId, CreateCreditCardDto req) {
        TouristEntity tourist = getTouristUseCase.getTouristById(touristId);

        CreditCardEntity creditCard = CreditCardMapper.INSTANCE.toEntity(req);
        creditCard = creditCardPort.save(creditCard);

        tourist.setCreditCardId(creditCard.getId());
        touristPort.save(tourist);

        // clear cache
        cachePort.deleteFromCache(CacheUtils.buildCacheKeyGetTouristById(tourist.getId()));

        return creditCard;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateAvatar(Long touristId, MultipartFile file) {
        TouristEntity tourist = getTouristUseCase.getTouristById(touristId);
        if (tourist == null) {
            log.error("Tourist not found with id: {}", touristId);
            throw new AppException(ErrorCode.TOURIST_NOT_FOUND);
        }

        var uploadResponse = fileStoragePort.uploadFiles(List.of(file));
        if (CollectionUtils.isEmpty(uploadResponse)) {
            throw new AppException(ErrorCode.UPLOAD_FILE_FAILED);
        }

        tourist.setAvatarUrl(uploadResponse.get(0).getUrl());

        touristPort.save(tourist);

        // clear cache
        cachePort.deleteFromCache(CacheUtils.buildCacheKeyGetTouristById(tourist.getId()));
    }
}
