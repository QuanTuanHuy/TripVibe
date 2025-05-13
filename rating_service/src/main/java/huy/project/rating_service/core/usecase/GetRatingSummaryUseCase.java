package huy.project.rating_service.core.usecase;

import huy.project.rating_service.core.domain.constant.CacheConstant;
import huy.project.rating_service.core.domain.entity.RatingSummaryEntity;
import huy.project.rating_service.core.port.ICachePort;
import huy.project.rating_service.core.port.IRatingSummaryPort;
import huy.project.rating_service.kernel.utils.CacheUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetRatingSummaryUseCase {
    private final IRatingSummaryPort ratingSummaryPort;
    private final ICachePort cachePort;

    public List<RatingSummaryEntity> getRatingSummariesByAccIds(List<Long> accIds) {
        return ratingSummaryPort.getRatingSummariesByAccIds(accIds);
    }

    public List<RatingSummaryEntity> getRatingSummariesToSync(int limit) {
        return ratingSummaryPort.getRatingSummariesNotSynced(limit);
    }

    public RatingSummaryEntity getRatingSummaryByAccId(Long accId) {
        String cacheKey = CacheUtils.buildCacheKeyGetRatingSummaryByAccId(accId);
        var cachedSummary = cachePort.getFromCache(cacheKey, RatingSummaryEntity.class);
        if (cachedSummary != null) {
            return cachedSummary;
        }

        var summary = ratingSummaryPort.getRatingSummaryByAccId(accId);
        if (summary != null) {
            cachePort.setToCache(cacheKey, summary, CacheConstant.DEFAULT_TTL);
        }

        return summary;
    }
}
