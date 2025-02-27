package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.domain.dto.request.CreateAmenityGroupRequestDto;
import huy.project.accommodation_service.core.domain.entity.AmenityGroupEntity;
import huy.project.accommodation_service.core.domain.mapper.AmenityGroupMapper;
import huy.project.accommodation_service.core.exception.AppException;
import huy.project.accommodation_service.core.port.IAmenityGroupPort;
import huy.project.accommodation_service.core.validation.AmenityGroupValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateAmenityGroupUseCase {
    private final IAmenityGroupPort amenityGroupPort;

    private final AmenityGroupValidation amenityGroupValidation;

    @Transactional(rollbackFor = Exception.class)
    public AmenityGroupEntity createAmenityGroup(CreateAmenityGroupRequestDto req) {
        Pair<Boolean, ErrorCode> validationResult = amenityGroupValidation.validateCreateAmenityGroupRequest(req);
        if (!validationResult.getFirst()) {
            log.error("Create amenity group failed, name is existed, {}", req.getName());
            throw new AppException(validationResult.getSecond());
        }

        AmenityGroupEntity amenityGroup = AmenityGroupMapper.INSTANCE.toEntity(req);
        return amenityGroupPort.save(amenityGroup);
    }
}
