package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.entity.UnitPriceGroupEntity;
import huy.project.accommodation_service.core.port.IUnitPriceGroupPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetUnitPriceGroupUseCase {
    private final IUnitPriceGroupPort unitPriceGroupPort;

    public List<UnitPriceGroupEntity> getUnitPriceGroupsByUnitIds(List<Long> unitIds) {
        return unitPriceGroupPort.getPriceGroupsByUnitIds(unitIds);
    }
}
