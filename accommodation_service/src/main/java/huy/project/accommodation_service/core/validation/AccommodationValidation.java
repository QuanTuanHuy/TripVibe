package huy.project.accommodation_service.core.validation;

import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.domain.dto.request.CreateAccommodationDtoV2;
import huy.project.accommodation_service.core.domain.entity.AccommodationEntity;
import huy.project.accommodation_service.core.domain.entity.UnitEntity;
import huy.project.accommodation_service.core.port.IAccommodationPort;
import huy.project.accommodation_service.core.port.IUnitPort;
import huy.project.accommodation_service.core.usecase.GetCurrencyUseCase;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccommodationValidation {
    IUnitPort unitPort;

    IAccommodationPort accommodationPort;

    LanguageValidation languageValidation;
    AmenityValidation amenityValidation;

    GetCurrencyUseCase getCurrencyUseCase;

//    public Pair<Boolean, ErrorCode> validateCreateAccommodationDto(CreateAccommodationDto req) {
//        AccommodationEntity existedAcc = accommodationPort.getAccommodationByName(req.getName());
//        if (existedAcc != null) {
//            return Pair.of(false, ErrorCode.ACCOMMODATION_NAME_EXISTED);
//        }
//
//        if (!languageValidation.languagesExist(req.getLanguageIds())) {
//            return Pair.of(false, ErrorCode.LANGUAGE_NOT_FOUND);
//        }
//
//        if (getCurrencyUseCase.getCurrencyById(req.getCurrencyId()) == null) {
//            return Pair.of(false, ErrorCode.CURRENCY_NOT_FOUND);
//        }
//
//        List<Long> amenityIds = req.getAmenities().stream()
//                .map(CreateAccommodationAmenityDto::getAmenityId)
//                .toList();
//        if (!amenityValidation.amenitiesExist(amenityIds)) {
//            return Pair.of(false, ErrorCode.AMENITY_NOT_FOUND);
//        }
//
//        return Pair.of(true, ErrorCode.SUCCESS);
//    }

    public Pair<Boolean, ErrorCode> validateCreateAccommodationDto(CreateAccommodationDtoV2 req) {
        AccommodationEntity existedAcc = accommodationPort.getAccommodationByName(req.getName());
        if (existedAcc != null) {
            return Pair.of(false, ErrorCode.ACCOMMODATION_NAME_EXISTED);
        }

        if (!languageValidation.languagesExist(req.getLanguageIds())) {
            return Pair.of(false, ErrorCode.LANGUAGE_NOT_FOUND);
        }

        if (!amenityValidation.amenitiesExist(req.getAmenityIds())) {
            return Pair.of(false, ErrorCode.AMENITY_NOT_FOUND);
        }

        return Pair.of(true, ErrorCode.SUCCESS);
    }

    public boolean accommodationExistToHost(Long userId, Long accId) {
        var existedAcc = accommodationPort.getAccommodationById(accId);
        return existedAcc != null && existedAcc.getHostId().equals(userId);
    }

    public Pair<Boolean, ErrorCode> isOwnerOfUnit(Long userId, Long unitId) {
        UnitEntity unit = unitPort.getUnitById(unitId);
        if (unit == null) {
            return Pair.of(false, ErrorCode.UNIT_NOT_FOUND);
        }
        var accommodation = accommodationPort.getAccommodationById(unit.getAccommodationId());
        if (accommodation == null) {
            return Pair.of(false, ErrorCode.ACCOMMODATION_NOT_FOUND);
        }
        var isOwner = accommodation.getHostId().equals(userId);
        if (!isOwner) {
            return Pair.of(false, ErrorCode.UNIT_NOT_BELONG_TO_HOST);
        }
        return Pair.of(true, ErrorCode.SUCCESS);
    }
}
