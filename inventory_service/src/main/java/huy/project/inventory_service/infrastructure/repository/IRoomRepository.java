package huy.project.inventory_service.infrastructure.repository;

import huy.project.inventory_service.infrastructure.repository.model.RoomModel;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IRoomRepository extends IBaseRepository<RoomModel> {
    List<RoomModel> findByUnitId(Long unitId);

    List<RoomModel> findByUnitIdIn(List<Long> unitIds);
}
