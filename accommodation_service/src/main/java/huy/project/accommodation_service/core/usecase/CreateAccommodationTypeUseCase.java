package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.dto.request.CreateAccommodationTypeDto;
import huy.project.accommodation_service.core.domain.entity.AccommodationTypeEntity;
import huy.project.accommodation_service.core.domain.mapper.AccommodationTypeMapper;
import huy.project.accommodation_service.core.exception.AppException;
import huy.project.accommodation_service.core.port.IAccommodationTypePort;
import huy.project.accommodation_service.core.validation.AccommodationTypeValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateAccommodationTypeUseCase {
    private final IAccommodationTypePort accommodationTypePort;

    private final AccommodationTypeValidation validator;

    public AccommodationTypeEntity createAccommodationType(CreateAccommodationTypeDto req) {
        var validationResult = validator.validateCreateAccommodationType(req);
        if (!validationResult.getFirst()) {
            log.error("Create accommodation type failed, err: {}", validationResult.getSecond().getMessage());
            throw new AppException(validationResult.getSecond());
        }

        AccommodationTypeEntity accommodationType = AccommodationTypeMapper.INSTANCE.toEntity(req);
        return accommodationTypePort.save(accommodationType);
    }
}
