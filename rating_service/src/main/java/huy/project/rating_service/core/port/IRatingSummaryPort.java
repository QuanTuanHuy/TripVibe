package huy.project.rating_service.core.port;

import huy.project.rating_service.core.domain.entity.RatingSummaryEntity;

import java.util.List;

public interface IRatingSummaryPort {
    void save(RatingSummaryEntity ratingSummary);
    void saveAll(List<RatingSummaryEntity> ratingSummaries);
    RatingSummaryEntity getRatingSummaryByAccId(Long accId);
    List<RatingSummaryEntity> getRatingSummariesByAccIds(List<Long> accIds);
    List<RatingSummaryEntity> getRatingSummariesNotSynced(int limit);
}
