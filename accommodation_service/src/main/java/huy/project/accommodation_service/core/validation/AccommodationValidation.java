package huy.project.accommodation_service.core.validation;

import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.domain.dto.request.CreateAccommodationAmenityDto;
import huy.project.accommodation_service.core.domain.dto.request.CreateAccommodationDto;
import huy.project.accommodation_service.core.domain.entity.AccommodationEntity;
import huy.project.accommodation_service.core.port.IAccommodationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccommodationValidation {
    private final IAccommodationPort accommodationPort;

    private final LanguageValidation languageValidation;
    private final AmenityValidation amenityValidation;

    public Pair<Boolean, ErrorCode> validateCreateAccommodationDto(CreateAccommodationDto req) {
        AccommodationEntity existedAcc = accommodationPort.getAccommodationByName(req.getName());
        if (existedAcc != null) {
            return Pair.of(false, ErrorCode.ACCOMMODATION_NAME_EXISTED);
        }

        if (!languageValidation.languagesExist(req.getLanguageIds())) {
            return Pair.of(false, ErrorCode.LANGUAGE_NOT_FOUND);
        }

        List<Long> amenityIds = req.getAmenities().stream()
                .map(CreateAccommodationAmenityDto::getAmenityId)
                .toList();
        if (!amenityValidation.amenitiesExist(amenityIds)) {
            return Pair.of(false, ErrorCode.AMENITY_NOT_FOUND);
        }

        return Pair.of(true, ErrorCode.SUCCESS);
    }

    public boolean accommodationExistToHost(Long userId, Long accId) {
        var existedAcc = accommodationPort.getAccommodationById(accId);
        return existedAcc != null && existedAcc.getHostId().equals(userId);
    }
}
