package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.exception.AppException;
import huy.project.accommodation_service.core.port.ICurrencyPort;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeleteCurrencyUseCase {
    ICurrencyPort currencyPort;

    @Transactional(rollbackFor = Exception.class)
    public void deleteCurrency(Long currencyId) {
        var currency = currencyPort.getCurrencyById(currencyId);
        if (currency == null) {
            throw new AppException(ErrorCode.CURRENCY_NOT_FOUND);
        }

        currencyPort.deleteById(currencyId);
    }
}
