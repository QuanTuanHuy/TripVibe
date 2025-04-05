package huy.project.accommodation_service.core.validation;

import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.domain.dto.request.CreateBedDto;
import huy.project.accommodation_service.core.domain.dto.request.CreateBedroomDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BedroomValidation {
    private final BedTypeValidation bedTypeValidation;

    public Pair<Boolean, ErrorCode> validateCreateBedroomDto(CreateBedroomDto req) {
        List<Long> bedTypeIds = req.getBeds().stream()
                .map(CreateBedDto::getBedTypeId)
                .toList();
        if (CollectionUtils.isEmpty(bedTypeIds) || !bedTypeValidation.bedTypesExist(bedTypeIds)) {
            return Pair.of(false, ErrorCode.BED_TYPE_NOT_FOUND);
        }

        return Pair.of(true, ErrorCode.SUCCESS);
    }
}
