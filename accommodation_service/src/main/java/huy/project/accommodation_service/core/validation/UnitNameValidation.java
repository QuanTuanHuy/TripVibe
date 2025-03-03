package huy.project.accommodation_service.core.validation;

import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.domain.dto.request.CreateUnitNameDto;
import huy.project.accommodation_service.core.port.IUnitNamePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UnitNameValidation {
    private final IUnitNamePort unitNamePort;

    public boolean isNameExisted(String name) {
        return unitNamePort.getUnitNameByName(name) != null;
    }

    public Pair<Boolean, ErrorCode> validateCreateRequest(CreateUnitNameDto req) {
        if (isNameExisted(req.getName())) {
            return Pair.of(false, ErrorCode.UNIT_NAME_EXISTED);
        }
        return Pair.of(true, ErrorCode.SUCCESS);
    }
}
