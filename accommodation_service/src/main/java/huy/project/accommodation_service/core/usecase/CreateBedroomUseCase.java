package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.dto.request.CreateBedroomDto;
import huy.project.accommodation_service.core.domain.entity.BedEntity;
import huy.project.accommodation_service.core.domain.entity.BedroomEntity;
import huy.project.accommodation_service.core.domain.mapper.BedroomMapper;
import huy.project.accommodation_service.core.exception.AppException;
import huy.project.accommodation_service.core.port.IBedPort;
import huy.project.accommodation_service.core.port.IBedroomPort;
import huy.project.accommodation_service.core.validation.BedroomValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateBedroomUseCase {
    private final IBedroomPort bedroomPort;
    private final IBedPort bedPort;

    private final BedroomValidation bedroomValidation;

    @Transactional(rollbackFor = Exception.class)
    public BedroomEntity createBedroom(Long unitId, CreateBedroomDto req) {
        // validate req
        var validationResult = bedroomValidation.validateCreateBedroomDto(req);
        if (!validationResult.getFirst()) {
            log.error("create bedroom failed: {}", validationResult.getSecond().getMessage());
            throw new AppException(validationResult.getSecond());
        }

        // create main bedroom
        BedroomEntity bedroom = BedroomMapper.INSTANCE.toEntity(unitId, req);
        bedroom = bedroomPort.save(bedroom);
        final Long bedroomId = bedroom.getId();

        // create beds
        List<BedEntity> beds = req.getBeds().stream()
                .map(bed -> BedEntity.builder()
                        .bedroomId(bedroomId)
                        .bedTypeId(bed.getBedTypeId())
                        .quantity(bed.getQuantity())
                        .build())
                .toList();
        bedPort.saveAll(beds);

        return bedroom;
    }
}
