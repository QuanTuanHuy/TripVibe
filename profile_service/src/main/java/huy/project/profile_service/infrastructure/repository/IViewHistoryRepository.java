package huy.project.profile_service.infrastructure.repository;

import huy.project.profile_service.infrastructure.repository.model.ViewHistoryModel;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IViewHistoryRepository extends IBaseRepository<ViewHistoryModel> {
    Optional<ViewHistoryModel> findByTouristIdAndAccommodationId(Long touristId, Long accId);
}
