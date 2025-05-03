package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.port.IPriceTypePort;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.SecondaryRow;

@SecondaryRow
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CreatePriceTypeUseCase {
    IPriceTypePort priceTypePort;


}
