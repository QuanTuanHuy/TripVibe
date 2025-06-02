package huy.project.rating_service.core.usecase;

import huy.project.rating_service.core.domain.entity.PendingReviewEntity;
import huy.project.rating_service.core.port.IPendingReviewPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeletePendingReviewUseCase {
    private final IPendingReviewPort pendingReviewPort;

    @Transactional(rollbackFor = Exception.class)
    public void deletePendingReviews(Long userId, List<Long> ids) {
        List<PendingReviewEntity> pendingReviews = pendingReviewPort.getPendingReviewsByIds(ids)
                .stream()
                .filter(pr -> pr.getUserId().equals(userId))
                .toList();
        if (!CollectionUtils.isEmpty(pendingReviews)) {
            List<Long> pendingReviewIds = pendingReviews.stream()
                    .map(PendingReviewEntity::getId)
                    .toList();
            pendingReviewPort.deleteByIds(pendingReviewIds);
        }
    }
}
