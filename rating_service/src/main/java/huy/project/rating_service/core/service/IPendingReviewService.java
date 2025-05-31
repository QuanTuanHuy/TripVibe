package huy.project.rating_service.core.service;

import huy.project.rating_service.core.domain.dto.request.PendingReviewParams;
import huy.project.rating_service.core.domain.dto.response.PageInfo;
import huy.project.rating_service.core.domain.dto.response.PendingReviewDto;
import huy.project.rating_service.core.domain.entity.PendingReviewEntity;
import org.springframework.data.util.Pair;

import java.util.List;

public interface IPendingReviewService {
    PendingReviewEntity createPendingReview(PendingReviewEntity pendingReview);
    Pair<PageInfo, List<PendingReviewDto>> getPendingReviews(PendingReviewParams params);
    void deletePendingReviews(Long userId, List<Long> ids);
}
