package huy.project.accommodation_service.core.port;

import huy.project.accommodation_service.core.domain.entity.BedEntity;

import java.util.List;

public interface IBedPort {
    List<BedEntity> saveAll(List<BedEntity> beds);
    List<BedEntity> getBedsByBedroomIds(List<Long> bedroomIds);
}
