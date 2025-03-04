package huy.project.accommodation_service.core.validation;

import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.domain.dto.request.CreateUnitAmenityDto;
import huy.project.accommodation_service.core.domain.dto.request.CreateUnitDto;
import huy.project.accommodation_service.core.domain.dto.request.CreateUnitPriceTypeDto;
import huy.project.accommodation_service.core.exception.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnitValidation {
    private final AmenityValidation amenityValidation;

    private final UnitNameValidation unitNameValidation;
    private final PriceTypeValidation priceTypeValidation;

    public Pair<Boolean, ErrorCode> validateCreateUnitDto(CreateUnitDto req) {
        if (!unitNameValidation.unitNameExist(req.getUnitNameId())) {
            return Pair.of(false, ErrorCode.UNIT_NAME_NOT_FOUND);
        }

        List<Long> amenityIds = req.getAmenities().stream()
                .map(CreateUnitAmenityDto::getAmenityId)
                .toList();
        if (!amenityValidation.amenitiesExist(amenityIds)) {
            return Pair.of(false, ErrorCode.AMENITY_NOT_FOUND);
        }

        List<Long> priceTypeIds = req.getPriceTypes().stream()
                .map(CreateUnitPriceTypeDto::getPriceTypeId)
                .toList();
        if (!priceTypeValidation.priceTypesExist(priceTypeIds)) {
            throw new AppException(ErrorCode.PRICE_TYPE_NOT_FOUND);
        }

        return Pair.of(true, ErrorCode.SUCCESS);
    }
}
