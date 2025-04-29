package huy.project.accommodation_service.infrastructure.repository;

import huy.project.accommodation_service.infrastructure.repository.model.LocationModel;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ILocationRepository extends IBaseRepository<LocationModel> {
    List<LocationModel> findByIdIn(List<Long> ids);
}
