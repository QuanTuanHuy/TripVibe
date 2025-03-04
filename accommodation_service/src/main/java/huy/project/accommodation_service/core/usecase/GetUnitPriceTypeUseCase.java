package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.entity.PriceTypeEntity;
import huy.project.accommodation_service.core.domain.entity.UnitPriceTypeEntity;
import huy.project.accommodation_service.core.port.IUnitPriceTypePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetUnitPriceTypeUseCase {
    private final IUnitPriceTypePort unitPriceTypePort;
    private final GetPriceTypeUseCase getPriceTypeUseCase;

    public List<UnitPriceTypeEntity> getUnitPriceTypesByUnitIds(List<Long> unitIds) {
        var unitPriceTypes = unitPriceTypePort.getUnitPricesByUnitIds(unitIds);

        List<PriceTypeEntity> priceTypes = getPriceTypeUseCase.getAllPriceTypes();
        var priceTypeMap = priceTypes.stream()
                .collect(Collectors.toMap(PriceTypeEntity::getId, Function.identity()));

        return unitPriceTypes.stream()
                .peek(unitPriceType -> unitPriceType.setPriceType(priceTypeMap.get(unitPriceType.getPriceTypeId())))
                .toList();
    }
}
