package huy.project.rating_service.core.usecase;

import huy.project.rating_service.core.domain.dto.request.PendingReviewParams;
import huy.project.rating_service.core.domain.entity.PendingReviewEntity;
import huy.project.rating_service.core.domain.exception.InvalidRequestException;
import huy.project.rating_service.core.port.IPendingReviewPort;
import huy.project.rating_service.core.port.IRatingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreatePendingReviewUseCase {
    private final IPendingReviewPort pendingReviewPort;
    private final IRatingPort ratingPort;

    @Transactional(rollbackFor = Exception.class)
    public PendingReviewEntity createPendingReview(PendingReviewEntity pendingReview) {
        validatePendingReview(pendingReview);
        if (ratingPort.isRatingExisted(pendingReview.getUserId(),
                pendingReview.getBookingId(),
                pendingReview.getUnitId())) {
            log.info("Rating already exists for userId: {}, bookingId: {}, unitId: {}",
                    pendingReview.getUserId(), pendingReview.getBookingId(), pendingReview.getUnitId());
            return null;
        }

        PendingReviewParams params = PendingReviewParams.builder()
                .userId(pendingReview.getUserId())
                .bookingId(pendingReview.getBookingId())
                .unitId(pendingReview.getUnitId())
                .build();
        if (!CollectionUtils.isEmpty(pendingReviewPort.getPendingReviews(params).getSecond())) {
            log.info("Pending review already exists for userId: {}, bookingId: {}, unitId: {}",
                    pendingReview.getUserId(), pendingReview.getBookingId(), pendingReview.getUnitId());
            return null;
        }

        return pendingReviewPort.save(pendingReview);
    }

    private void validatePendingReview(PendingReviewEntity pendingReview) {
        if (pendingReview.getUserId() == null || pendingReview.getAccommodationId() == null ||
            pendingReview.getUnitId() == null || pendingReview.getBookingId() == null) {
            throw new InvalidRequestException("Invalid pending review data");
        }
    }
}
