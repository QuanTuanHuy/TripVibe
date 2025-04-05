package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.domain.dto.request.CreateUnitNameDto;
import huy.project.accommodation_service.core.domain.entity.UnitNameEntity;
import huy.project.accommodation_service.core.domain.mapper.UnitNameMapper;
import huy.project.accommodation_service.core.exception.AppException;
import huy.project.accommodation_service.core.port.IUnitNamePort;
import huy.project.accommodation_service.core.validation.UnitNameValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateUnitNameUseCase {
    private final IUnitNamePort unitNamePort;

    private final UnitNameValidation unitNameValidation;

    @Transactional(rollbackFor = Exception.class)
    public UnitNameEntity createUnitName(CreateUnitNameDto req) {
        Pair<Boolean, ErrorCode> validationResult = unitNameValidation.validateCreateRequest(req);
        if (!validationResult.getFirst()) {
            log.error("Create unit name failed, name is existed, {}", req.getName());
            throw new AppException(validationResult.getSecond());
        }

        UnitNameEntity unitName = UnitNameMapper.INSTANCE.toEntity(req);
        return unitNamePort.save(unitName);
    }
}
