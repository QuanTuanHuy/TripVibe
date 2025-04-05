package huy.project.accommodation_service.core.port;

import huy.project.accommodation_service.core.domain.entity.BedroomEntity;

import java.util.List;

public interface IBedroomPort {
    BedroomEntity save(BedroomEntity bedroom);
    List<BedroomEntity> getBedroomsByUnitIds(List<Long> unitIds);
    List<BedroomEntity> getBedroomsByUnitId(Long unitId);
}
