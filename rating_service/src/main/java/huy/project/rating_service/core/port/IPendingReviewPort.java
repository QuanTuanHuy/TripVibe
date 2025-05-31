package huy.project.rating_service.core.port;

import huy.project.rating_service.core.domain.dto.request.PendingReviewParams;
import huy.project.rating_service.core.domain.dto.response.PageInfo;
import huy.project.rating_service.core.domain.entity.PendingReviewEntity;
import org.springframework.data.util.Pair;

import java.util.List;

public interface IPendingReviewPort {
    PendingReviewEntity save(PendingReviewEntity pendingReview);
    Pair<PageInfo, List<PendingReviewEntity>> getPendingReviews(PendingReviewParams params);
    List<PendingReviewEntity> getPendingReviewsByIds(List<Long> ids);
    void deleteByIds(List<Long> ids);
}
