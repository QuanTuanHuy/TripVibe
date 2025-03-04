package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.dto.request.UnitNameParams;
import huy.project.accommodation_service.core.domain.dto.response.PageInfo;
import huy.project.accommodation_service.core.domain.entity.UnitNameEntity;
import huy.project.accommodation_service.core.port.IUnitNamePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetUnitNameUseCase {
    private final IUnitNamePort unitNamePort;

    public Pair<PageInfo, List<UnitNameEntity>> getUnitNames(UnitNameParams params) {
        return unitNamePort.getUnitNames(params);
    }

    public List<UnitNameEntity> getUnitNamesByIds(List<Long> ids) {
        return unitNamePort.getUnitNamesByIds(ids);
    }
}
