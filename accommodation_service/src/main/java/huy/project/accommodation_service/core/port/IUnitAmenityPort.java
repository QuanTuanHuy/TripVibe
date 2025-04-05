package huy.project.accommodation_service.core.port;

import huy.project.accommodation_service.core.domain.entity.UnitAmenityEntity;

import java.util.List;

public interface IUnitAmenityPort {
    List<UnitAmenityEntity> saveAll(List<UnitAmenityEntity> unitAmenities);
    List<UnitAmenityEntity> getUnitAmenitiesByUnitIds(List<Long> unitIds);
    List<UnitAmenityEntity> getUnitAmenitiesByUnitId(Long unitId);
    void deleteByUnitIdAndAmenityIdIn(Long unitId, List<Long> amenityIds);
}
