package huy.project.accommodation_service.core.validation;

import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.domain.dto.request.CreateAccommodationTypeDto;
import huy.project.accommodation_service.core.port.IAccommodationTypePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccommodationTypeValidation {
    private final IAccommodationTypePort accommodationTypePort;

    public Pair<Boolean, ErrorCode> validateCreateAccommodationType(CreateAccommodationTypeDto req) {
        if (accommodationTypePort.getAccommodationTypeByName(req.getName()) != null) {
            return Pair.of(false, ErrorCode.ACCOMMODATION_TYPE_NAME_EXISTED);
        }
        return Pair.of(true, ErrorCode.SUCCESS);
    }
}
