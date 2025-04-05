package huy.project.profile_service.infrastructure.repository;

import huy.project.profile_service.infrastructure.repository.model.TouristModel;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ITouristRepository extends IBaseRepository<TouristModel> {
    List<TouristModel> findByIdIn(List<Long> ids);
}
