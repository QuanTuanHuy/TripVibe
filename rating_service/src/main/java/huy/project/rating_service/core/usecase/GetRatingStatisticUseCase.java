package huy.project.rating_service.core.usecase;

import huy.project.rating_service.core.domain.constant.ErrorCode;
import huy.project.rating_service.core.domain.dto.response.RatingStatisticDto;
import huy.project.rating_service.core.domain.exception.AppException;
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

    public RatingStatisticDto getStatisticByAccId(Long accommodationId) {
        var ratingSummary = getRatingSummaryUseCase.getRatingSummaryByAccId(accommodationId);
        if (ratingSummary == null) {
            log.error("Accommodation {} not found", accommodationId);
            throw new AppException(ErrorCode.ACCOMMODATION_NOT_FOUND);
        }

        return RatingStatisticDto.builder()
                .accommodationId(accommodationId)
                .overallAverage(ratingSummary.getNumberOfRatings () == 0 ? 0.0
                        : (double) ratingSummary.getTotalRating() / ratingSummary.getNumberOfRatings())
                .criteriaAverages(ratingSummary.getCriteriaAverages())
                .totalRatings(ratingSummary.getNumberOfRatings())
                .ratingDistribution(ratingSummary.getDistribution())
                .build();
    }
}
