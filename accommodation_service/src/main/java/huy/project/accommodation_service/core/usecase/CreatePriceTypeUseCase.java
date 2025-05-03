package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.domain.dto.request.CreatePriceTypeDto;
import huy.project.accommodation_service.core.domain.entity.PriceTypeEntity;
import huy.project.accommodation_service.core.domain.mapper.PriceTypeMapper;
import huy.project.accommodation_service.core.exception.AppException;
import huy.project.accommodation_service.core.port.IPriceTypePort;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CreatePriceTypeUseCase {
    IPriceTypePort priceTypePort;

    @Transactional(rollbackFor = Exception.class)
    public PriceTypeEntity createPriceType(CreatePriceTypeDto req) {
        PriceTypeEntity existedPriceType = priceTypePort.getPriceTypeByName(req.getName());
        if (existedPriceType != null) {
            throw new AppException(ErrorCode.PRICE_TYPE_NAME_EXISTED);
        }

        PriceTypeEntity priceType = PriceTypeMapper.INSTANCE.toEntity(req);
        return priceTypePort.save(priceType);
    }
}
