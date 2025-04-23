package huy.project.accommodation_service.core.port;

import huy.project.accommodation_service.core.domain.entity.UnitEntity;

import java.util.List;

public interface IUnitPort {
    UnitEntity save(UnitEntity unit);
    List<UnitEntity> getUnitsByAccommodationId(Long accommodationId);
    UnitEntity getUnitByAccIdAndId(Long accId, Long id);
    void deleteUnitsByAccId(Long accId);
    List<UnitEntity> getUnitsByIds(List<Long> ids);
    UnitEntity getUnitById(Long id);
}
