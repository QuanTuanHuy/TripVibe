package huy.project.accommodation_service.core.port;

import huy.project.accommodation_service.core.domain.dto.request.BedTypeParams;
import huy.project.accommodation_service.core.domain.dto.response.PageInfo;
import huy.project.accommodation_service.core.domain.entity.BedTypeEntity;
import org.springframework.data.util.Pair;

import java.util.List;

public interface IBedTypePort {
    BedTypeEntity save(BedTypeEntity bedType);
    BedTypeEntity getBedTypeByName(String name);
    Pair<PageInfo, List<BedTypeEntity>> getBedTypes(BedTypeParams params);
    List<BedTypeEntity> getBedTypesByIds(List<Long> ids);
}
