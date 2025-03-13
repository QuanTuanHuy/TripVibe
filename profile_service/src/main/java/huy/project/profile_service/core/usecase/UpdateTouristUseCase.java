package huy.project.profile_service.core.usecase;

import huy.project.profile_service.core.domain.constant.ErrorCode;
import huy.project.profile_service.core.domain.dto.request.CreateCreditCardDto;
import huy.project.profile_service.core.domain.dto.request.UpdateTouristDto;
import huy.project.profile_service.core.domain.entity.*;
import huy.project.profile_service.core.domain.exception.AppException;
import huy.project.profile_service.core.domain.mapper.*;
import huy.project.profile_service.core.port.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return tourist;
    }

    @Transactional(rollbackFor = Exception.class)
    public CreditCardEntity addCreditCardToTourist(Long touristId, CreateCreditCardDto req) {
        TouristEntity tourist = getTouristUseCase.getTouristById(touristId);

        CreditCardEntity creditCard = CreditCardMapper.INSTANCE.toEntity(req);
        creditCard = creditCardPort.save(creditCard);

        tourist.setCreditCardId(creditCard.getId());
        touristPort.save(tourist);

        return creditCard;
    }
}
