package huy.project.accommodation_service.core.validation;

import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.domain.dto.request.CreateAmenityGroupRequestDto;
import huy.project.accommodation_service.core.domain.dto.request.UpdateAmenityGroupRequestDto;
import huy.project.accommodation_service.core.domain.entity.AmenityGroupEntity;
import huy.project.accommodation_service.core.port.IAmenityGroupPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AmenityGroupValidation {
    private final IAmenityGroupPort amenityGroupPort;

    private boolean isNameExisted(String name) {
        return amenityGroupPort.getAmenityGroupByName(name) != null;
    }

    public Pair<Boolean, ErrorCode> validateCreateAmenityGroupRequest(CreateAmenityGroupRequestDto req) {
        if (isNameExisted(req.getName())) {
            return Pair.of(false, ErrorCode.AMENITY_GROUP_NAME_EXISTED);
        }
        return Pair.of(true, ErrorCode.SUCCESS);
    }

    public Pair<Boolean, ErrorCode> validateUpdateAmenityGroupRequest(
            AmenityGroupEntity existedAmenityGroup, UpdateAmenityGroupRequestDto req) {
        if (!existedAmenityGroup.getName().equals(req.getName())) {
            if (isNameExisted(req.getName())) {
                return Pair.of(false, ErrorCode.AMENITY_GROUP_NAME_EXISTED);
            }
        }
        return Pair.of(true, ErrorCode.SUCCESS);
    }

    public boolean isAmenityGroupExisted(Long id) {
        return amenityGroupPort.getAmenityGroupById(id) != null;
    }
}
