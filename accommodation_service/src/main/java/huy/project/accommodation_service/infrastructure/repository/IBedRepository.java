package huy.project.accommodation_service.infrastructure.repository;

import huy.project.accommodation_service.infrastructure.repository.model.BedModel;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IBedRepository extends IBaseRepository<BedModel> {
    List<BedModel> findByBedroomIdIn(List<Long> bedroomIds);
}
