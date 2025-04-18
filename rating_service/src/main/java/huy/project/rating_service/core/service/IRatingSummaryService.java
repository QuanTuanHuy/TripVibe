package huy.project.rating_service.core.service;

import huy.project.rating_service.core.domain.entity.RatingSummaryEntity;

import java.util.List;

public interface IRatingSummaryService {
    List<RatingSummaryEntity> getRatingSummariesByAccIds(List<Long> accIds);
    void createRatingSummary(RatingSummaryEntity ratingSummary);
    List<RatingSummaryEntity> getRatingSummariesToSync(int limit);
    void updateRatingSummaries(List<RatingSummaryEntity> ratingSummaries);
}
