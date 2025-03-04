package huy.project.accommodation_service.core.port;

import huy.project.accommodation_service.core.domain.entity.UnitEntity;

public interface IUnitPort {
    UnitEntity save(UnitEntity unit);
}
