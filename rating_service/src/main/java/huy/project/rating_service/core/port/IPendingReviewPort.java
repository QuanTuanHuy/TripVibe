package huy.project.rating_service.core.port;

import com.nimbusds.jose.util.Pair;
import huy.project.rating_service.core.domain.dto.request.PendingReviewParams;
import huy.project.rating_service.core.domain.dto.response.PageInfo;
import huy.project.rating_service.core.domain.entity.PendingReviewEntity;

import java.util.List;

public interface IPendingReviewPort {
    PendingReviewEntity save(PendingReviewEntity pendingReview);
    Pair<PageInfo, List<PendingReviewEntity>> getPendingReviews(PendingReviewParams params);
    void deleteByIds(List<Long> ids);
}
