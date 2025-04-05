package huy.project.accommodation_service.core.port;

import huy.project.accommodation_service.core.domain.dto.request.UnitNameParams;
import huy.project.accommodation_service.core.domain.dto.response.PageInfo;
import huy.project.accommodation_service.core.domain.entity.UnitNameEntity;
import org.springframework.data.util.Pair;

import java.util.List;

public interface IUnitNamePort {
    UnitNameEntity save(UnitNameEntity unitName);
    UnitNameEntity getUnitNameByName(String name);
    Pair<PageInfo, List<UnitNameEntity>> getUnitNames(UnitNameParams params);
    List<UnitNameEntity> getUnitNamesByIds(List<Long> ids);
    UnitNameEntity getUnitNameById(Long id);
}
