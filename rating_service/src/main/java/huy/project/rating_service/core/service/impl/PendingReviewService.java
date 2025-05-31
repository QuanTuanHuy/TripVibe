package huy.project.rating_service.core.service.impl;

import huy.project.rating_service.core.domain.dto.request.PendingReviewParams;
import huy.project.rating_service.core.domain.dto.response.PageInfo;
import huy.project.rating_service.core.domain.dto.response.PendingReviewDto;
import huy.project.rating_service.core.domain.entity.PendingReviewEntity;
import huy.project.rating_service.core.service.IPendingReviewService;
import huy.project.rating_service.core.usecase.CreatePendingReviewUseCase;
import huy.project.rating_service.core.usecase.DeletePendingReviewUseCase;
import huy.project.rating_service.core.usecase.GetPendingReviewUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PendingReviewService implements IPendingReviewService {
    private final CreatePendingReviewUseCase createPendingReviewUseCase;
    private final GetPendingReviewUseCase getPendingReviewUseCase;
    private final DeletePendingReviewUseCase deletePendingReviewUseCase;

    @Override
    public PendingReviewEntity createPendingReview(PendingReviewEntity pendingReview) {
        return createPendingReviewUseCase.createPendingReview(pendingReview);
    }

    @Override
    public Pair<PageInfo, List<PendingReviewDto>> getPendingReviews(PendingReviewParams params) {
        return getPendingReviewUseCase.getPendingReviews(params);
    }

    @Override
    public void deletePendingReviews(Long userId, List<Long> ids) {
        deletePendingReviewUseCase.deletePendingReviews(userId, ids);
    }
}
