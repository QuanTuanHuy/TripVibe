package huy.project.rating_service.infrastructure.repository;

import huy.project.rating_service.infrastructure.repository.model.RatingSummaryModel;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IRatingSummaryRepository extends IBaseRepository<RatingSummaryModel> {
    Optional<RatingSummaryModel> findByAccommodationId(Long accId);
    List<RatingSummaryModel> findByAccommodationIdIn(List<Long> accIds);
    List<RatingSummaryModel> findByIsSyncedWithSearchService(Boolean isSynced, Pageable pageable);
}
