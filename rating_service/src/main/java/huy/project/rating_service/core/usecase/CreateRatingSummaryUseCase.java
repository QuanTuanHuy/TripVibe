package huy.project.rating_service.core.usecase;

import huy.project.rating_service.core.domain.entity.RatingSummaryEntity;
import huy.project.rating_service.core.domain.entity.RatingTrendEntity;
import huy.project.rating_service.core.port.IRatingSummaryPort;
import huy.project.rating_service.core.port.IRatingTrendPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateRatingSummaryUseCase {
    private final IRatingSummaryPort ratingSummaryPort;
    private final IRatingTrendPort ratingTrendPort;

    @Transactional(rollbackFor = Exception.class)
    public void createRatingSummary(RatingSummaryEntity ratingSummary) {
        var existedRatingSummary = ratingSummaryPort.getRatingSummaryByAccId(ratingSummary.getAccommodationId());
        if (existedRatingSummary != null) {
            log.info("Rating Summary already existed, skipping Create Rating Summary");
            return;
        }
        ratingSummaryPort.save(ratingSummary);

        // create rating trend
        var exitedRatingTrend = ratingTrendPort.getRatingTrendByAccId(ratingSummary.getAccommodationId());
        if (exitedRatingTrend == null) {
            var ratingTrend = RatingTrendEntity.builder()
                    .accommodationId(ratingSummary.getAccommodationId())
                    .build();
            ratingTrendPort.save(ratingTrend);
        }
    }
}
