package huy.project.accommodation_service.core.validation;

import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.domain.dto.request.UpdateAmenityRequestDto;
import huy.project.accommodation_service.core.domain.entity.AmenityEntity;
import huy.project.accommodation_service.core.domain.dto.request.CreateAmenityRequestDto;
import huy.project.accommodation_service.core.port.IAmenityPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AmenityValidation {
    private final IAmenityPort amenityPort;

    private final AmenityGroupValidation amenityGroupValidation;

    private boolean isAmenityNameExist(String name) {
        return amenityPort.getAmenityByName(name) != null;
    }

    public Pair<Boolean, ErrorCode> validateCreateAmenityRequest(CreateAmenityRequestDto req) {
        if (isAmenityNameExist(req.getName())) {
            return Pair.of(false, ErrorCode.AMENITY_NAME_EXISTED);
        }

        if (!amenityGroupValidation.isAmenityGroupExisted(req.getGroupId())) {
            return Pair.of(false, ErrorCode.AMENITY_GROUP_NOT_FOUND);
        }

        return Pair.of(true, ErrorCode.SUCCESS);
    }

    public Pair<Boolean, ErrorCode> validateUpdateAmenityRequest(AmenityEntity existedAmenity, UpdateAmenityRequestDto req) {
        if (!existedAmenity.getName().equals(req.getName())) {
            if (isAmenityNameExist(req.getName())) {
                return Pair.of(false, ErrorCode.AMENITY_NAME_EXISTED);
            }
        }

        if (!amenityGroupValidation.isAmenityGroupExisted(req.getGroupId())) {
            return Pair.of(false, ErrorCode.AMENITY_GROUP_NOT_FOUND);
        }

        return Pair.of(true, ErrorCode.SUCCESS);
    }
}
