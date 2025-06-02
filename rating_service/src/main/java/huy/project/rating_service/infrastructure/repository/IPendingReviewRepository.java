package huy.project.rating_service.infrastructure.repository;

import huy.project.rating_service.infrastructure.repository.model.PendingReviewModel;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPendingReviewRepository extends IBaseRepository<PendingReviewModel> {
    void deleteByIdIn(List<Long> ids);
    List<PendingReviewModel> findByIdIn(List<Long> ids);
}
