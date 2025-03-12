package huy.project.profile_service.core.usecase;

import huy.project.profile_service.core.domain.constant.ErrorCode;
import huy.project.profile_service.core.domain.entity.TouristEntity;
import huy.project.profile_service.core.domain.exception.AppException;
import huy.project.profile_service.core.port.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetTouristUseCase {
    private final ITouristPort touristPort;
    private final ILocationPort locationPort;
    private final IPassportPort passportPort;
    private final ICreditCardPort creditCardPort;

    public TouristEntity getTouristById(Long id) {
        var tourist = touristPort.getTouristById(id);
        if (tourist == null) {
            log.error("getTouristById: tourist not found, id: {}", id);
            throw new AppException(ErrorCode.TOURIST_NOT_FOUND);
        }
        return tourist;
    }

    public TouristEntity getDetailTourist(Long id) {
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
        return tourist;
    }
}
