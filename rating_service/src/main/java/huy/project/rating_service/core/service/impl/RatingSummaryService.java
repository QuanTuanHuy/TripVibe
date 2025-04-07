package huy.project.rating_service.core.service.impl;

import huy.project.rating_service.core.domain.entity.RatingSummaryEntity;
import huy.project.rating_service.core.service.IRatingSummaryService;
import huy.project.rating_service.core.usecase.CreateRatingSummaryUseCase;
import huy.project.rating_service.core.usecase.GetRatingSummaryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingSummaryService implements IRatingSummaryService {
    private final GetRatingSummaryUseCase getRatingSummaryUseCase;
    private final CreateRatingSummaryUseCase createRatingSummaryUseCase;

    @Override
    public List<RatingSummaryEntity> getRatingSummariesByAccIds(List<Long> accIds) {
        return getRatingSummaryUseCase.getRatingSummariesByAccIds(accIds);
    }

    @Override
    public void createRatingSummary(RatingSummaryEntity ratingSummary) {
        createRatingSummaryUseCase.createRatingSummary(ratingSummary);
    }
}
