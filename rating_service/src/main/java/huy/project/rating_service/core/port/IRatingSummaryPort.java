package huy.project.rating_service.core.port;

import huy.project.rating_service.core.domain.entity.RatingSummaryEntity;

import java.util.List;

public interface IRatingSummaryPort {
    void save(RatingSummaryEntity ratingSummary);
    RatingSummaryEntity getRatingSummaryByAccId(Long accId);
    List<RatingSummaryEntity> getRatingSummariesByAccIds(List<Long> accIds);
}
