package huy.project.rating_service.core.usecase;

import huy.project.rating_service.core.domain.constant.CacheConstant;
import huy.project.rating_service.core.domain.constant.ErrorCode;
import huy.project.rating_service.core.domain.dto.response.RatingStatisticDto;
import huy.project.rating_service.core.domain.exception.AppException;
import huy.project.rating_service.core.port.ICachePort;
import huy.project.rating_service.kernel.utils.CacheUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GetRatingStatisticUseCase {
    GetRatingSummaryUseCase getRatingSummaryUseCase;
    ICachePort cachePort;

    public RatingStatisticDto getStatisticByAccId(Long accommodationId) {
        String cacheKey = CacheUtils.buildCacheKeyGetRatingStatisticByAccId(accommodationId);
        var cachedStatistic = cachePort.getFromCache(cacheKey, RatingStatisticDto.class);
        if (cachedStatistic != null) {
            return cachedStatistic;
        }

        var ratingSummary = getRatingSummaryUseCase.getRatingSummaryByAccId(accommodationId);
        if (ratingSummary == null) {
            log.error("Accommodation {} not found", accommodationId);
            throw new AppException(ErrorCode.ACCOMMODATION_NOT_FOUND);
        }

        var result = RatingStatisticDto.builder()
                .accommodationId(accommodationId)
                .overallAverage(ratingSummary.getNumberOfRatings () == 0 ? null
                        : (double) ratingSummary.getTotalRating() / ratingSummary.getNumberOfRatings())
                .criteriaAverages(ratingSummary.getCriteriaAverages())
                .totalRatings(ratingSummary.getNumberOfRatings())
                .ratingDistribution(ratingSummary.getDistribution())
                .build();

        cachePort.setToCache(cacheKey, result, CacheConstant.DEFAULT_TTL);
        return result;
    }
}
