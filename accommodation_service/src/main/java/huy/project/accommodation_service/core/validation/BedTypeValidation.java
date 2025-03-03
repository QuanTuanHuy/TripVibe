package huy.project.accommodation_service.core.validation;

import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.domain.dto.request.CreateBedTypeDto;
import huy.project.accommodation_service.core.domain.entity.BedTypeEntity;
import huy.project.accommodation_service.core.port.IBedTypePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BedTypeValidation {
    private final IBedTypePort bedTypePort;

    public Pair<Boolean, ErrorCode> validateCreateBedTypeDto(CreateBedTypeDto req) {
        BedTypeEntity existedBedType = bedTypePort.getBedTypeByName(req.getName());
        if (existedBedType != null) {
            return Pair.of(false, ErrorCode.BED_TYPE_NAME_EXISTED);
        }

        return Pair.of(true, ErrorCode.SUCCESS);
    }
}
