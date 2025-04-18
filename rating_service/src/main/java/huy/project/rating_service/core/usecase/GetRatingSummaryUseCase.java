package huy.project.rating_service.core.usecase;

import huy.project.rating_service.core.domain.entity.RatingSummaryEntity;
import huy.project.rating_service.core.port.IRatingSummaryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetRatingSummaryUseCase {
    private final IRatingSummaryPort ratingSummaryPort;

    public List<RatingSummaryEntity> getRatingSummariesByAccIds(List<Long> accIds) {
        return ratingSummaryPort.getRatingSummariesByAccIds(accIds);
    }

    public List<RatingSummaryEntity> getRatingSummariesToSync(int limit) {
        return ratingSummaryPort.getRatingSummariesNotSynced(limit);
    }
}
