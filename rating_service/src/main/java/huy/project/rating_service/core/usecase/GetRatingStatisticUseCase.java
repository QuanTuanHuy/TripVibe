package huy.project.rating_service.core.usecase;

import huy.project.rating_service.core.domain.constant.ErrorCode;
import huy.project.rating_service.core.domain.dto.response.RatingStatisticDto;
import huy.project.rating_service.core.domain.exception.AppException;
import huy.project.rating_service.core.port.IRatingDetailPort;
import huy.project.rating_service.core.port.IRatingSummaryPort;
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
    IRatingSummaryPort ratingSummaryPort;
    IRatingDetailPort ratingDetailPort;

    public RatingStatisticDto getStatisticByAccId(Long accommodationId) {
        var ratingSummary = ratingSummaryPort.getRatingSummaryByAccId(accommodationId);
        if (ratingSummary == null) {
            log.error("Accommodation {} not found", accommodationId);
            throw new AppException(ErrorCode.ACCOMMODATION_NOT_FOUND);
        }

        var criteriaAverages = ratingDetailPort.getAverageRatingsByCriteriaForAccommodation(accommodationId);

        return RatingStatisticDto.builder()
                .accommodationId(accommodationId)
                .overallAverage(ratingSummary.getNumberOfRatings () == 0 ? null
                        : (double) ratingSummary.getTotalRating() / ratingSummary.getNumberOfRatings())
                .criteriaAverages(criteriaAverages)
                .totalRatings(ratingSummary.getNumberOfRatings())
                .ratingDistribution(ratingSummary.getDistribution())
                .build();
    }
}
