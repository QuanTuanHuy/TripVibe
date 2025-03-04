package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.domain.entity.PriceTypeEntity;
import huy.project.accommodation_service.core.exception.AppException;
import huy.project.accommodation_service.core.port.IPriceTypePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetPriceTypeUseCase {
    private final IPriceTypePort priceTypePort;

    public PriceTypeEntity getPriceTypeById(Long id) {
        PriceTypeEntity priceType = priceTypePort.getPriceTypeById(id);
        if (priceType == null) {
            log.error("Price type with id {} not found", id);
            throw new AppException(ErrorCode.PRICE_TYPE_NOT_FOUND);
        }
        return priceType;
    }

    public List<PriceTypeEntity> getAllPriceTypes() {
        return priceTypePort.getAllPriceTypes();
    }
}
