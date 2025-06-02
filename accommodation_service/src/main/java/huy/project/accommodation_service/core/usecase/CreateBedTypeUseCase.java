package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.dto.request.CreateBedTypeDto;
import huy.project.accommodation_service.core.domain.entity.BedTypeEntity;
import huy.project.accommodation_service.core.domain.mapper.BedTypeMapper;
import huy.project.accommodation_service.core.exception.AppException;
import huy.project.accommodation_service.core.port.IBedTypePort;
import huy.project.accommodation_service.core.validation.BedTypeValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateBedTypeUseCase {
    private final IBedTypePort bedTypePort;
    private final BedTypeValidation bedTypeValidation;

    @Transactional(rollbackFor = Exception.class)
    public BedTypeEntity createBedType(CreateBedTypeDto req) {
        var validationResult = bedTypeValidation.validateCreateBedTypeDto(req);
        if (!validationResult.getFirst()) {
            log.error("Create bed type failed, err: {}", validationResult.getSecond().getMessage());
            throw new AppException(validationResult.getSecond());
        }

        BedTypeEntity bedType = BedTypeMapper.INSTANCE.toEntity(req);
        return bedTypePort.save(bedType);
    }

    @Transactional(rollbackFor = Exception.class)
    public void createIfNotExists(List<CreateBedTypeDto> bedTypes) {
        long existingCount = bedTypePort.countAll();
        if (existingCount > 0) {
            log.info("Bed types already exist, skipping creation.");
            return;
        }

        bedTypes.forEach(bedType -> {
            try {
                createBedType(bedType);
            } catch (Exception e) {
                log.warn("Failed to create bed type: {}, error: {}", bedType.getName(), e.getMessage());
            }
        });
    }
}
