package huy.project.rating_service.core.usecase;

import huy.project.rating_service.core.domain.entity.RatingSummaryEntity;
import huy.project.rating_service.core.port.IRatingSummaryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateRatingSummaryUseCase {
    private final IRatingSummaryPort ratingSummaryPort;

    @Transactional(rollbackFor = Exception.class)
    public void createRatingSummary(RatingSummaryEntity ratingSummary) {
        var existedRatingSummary = ratingSummaryPort.getRatingSummaryByAccId(ratingSummary.getAccommodationId());
        if (existedRatingSummary != null) {
            log.info("Rating Summary already existed, skipping Create Rating Summary");
            return;
        }
        ratingSummaryPort.save(ratingSummary);
    }
}
