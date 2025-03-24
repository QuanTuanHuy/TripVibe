package huy.project.rating_service.core.port;

import huy.project.rating_service.core.domain.dto.response.UnitDto;

import java.util.List;

public interface IAccommodationPort {
    List<UnitDto> getUnitsByIds(List<Long> unitIds);
}
