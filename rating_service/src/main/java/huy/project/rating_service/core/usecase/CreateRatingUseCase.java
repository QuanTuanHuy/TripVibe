package huy.project.rating_service.core.usecase;

import huy.project.rating_service.core.domain.constant.ErrorCode;
import huy.project.rating_service.core.domain.constant.RatingCriteriaType;
import huy.project.rating_service.core.domain.dto.request.CreateRatingDto;
import huy.project.rating_service.core.domain.entity.MonthlyRatingEntity;
import huy.project.rating_service.core.domain.entity.RatingDetailEntity;
import huy.project.rating_service.core.domain.entity.RatingEntity;
import huy.project.rating_service.core.domain.exception.AppException;
import huy.project.rating_service.core.domain.mapper.RatingMapper;
import huy.project.rating_service.core.port.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CreateRatingUseCase {
    private static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-M");

    IRatingPort ratingPort;
    IRatingDetailPort ratingDetailPort;
    IBookingPort bookingPort;
    IRatingSummaryPort ratingSummaryPort;
    IRatingTrendPort ratingTrendPort;
    IMonthlyRatingPort monthlyRatingPort;

    @Transactional(rollbackFor = Exception.class)
    public RatingEntity createRating(CreateRatingDto req) {
        validateRequest(req);

        RatingEntity rating = createRatingEntity(req);

        saveRatingDetails(rating, req);

        updateRatingSummary(req);

        updateRatingTrend(req);

        return rating;
    }

    private RatingEntity createRatingEntity(CreateRatingDto req) {
        var rating = RatingMapper.INSTANCE.toEntity(req);
        rating.setNumberOfHelpful(0);
        rating.setNumberOfUnhelpful(0);
        return ratingPort.save(rating);
    }

    private void saveRatingDetails(RatingEntity rating, CreateRatingDto req) {
        List<RatingDetailEntity> details = req.getRatingDetails().entrySet().stream()
                .map(entry -> RatingDetailEntity.builder()
                        .ratingId(rating.getId())
                        .value(entry.getValue())
                        .criteriaType(entry.getKey())
                        .build())
                .toList();

        rating.setRatingDetails(ratingDetailPort.saveAll(details));
    }

    private void updateRatingSummary(CreateRatingDto req) {
        var ratingSummary = ratingSummaryPort.getRatingSummaryByAccId(req.getAccommodationId());
        if (ratingSummary == null) {
            log.error("Accommodation {} not found", req.getAccommodationId());
            throw new AppException(ErrorCode.ACCOMMODATION_NOT_FOUND);
        }

        // Increment rating count and update total
        ratingSummary.setNumberOfRatings(ratingSummary.getNumberOfRatings() + 1);
        ratingSummary.setTotalRating(
                ratingSummary.getTotalRating() + req.getValue().longValue());
        ratingSummary.setIsSyncedWithSearchService(false);

        // Update distribution
        Map<Integer, Integer> distribution = ratingSummary.getDistribution();
        int ratingValueInt = req.getValue().intValue();
        distribution.put(ratingValueInt, distribution.getOrDefault(ratingValueInt, 0) + 1);

        // Update criteria averages
        Map<RatingCriteriaType, Double> criteriaAverages = ratingSummary.getCriteriaAverages();
        if (criteriaAverages == null) {
            criteriaAverages = new HashMap<>();
            ratingSummary.setCriteriaAverages(criteriaAverages);
        }

        // Calculate averages for each criteria
        int newNumberOfRatings = ratingSummary.getNumberOfRatings();
        for (Map.Entry<RatingCriteriaType, Double> entry : req.getRatingDetails().entrySet()) {
            RatingCriteriaType criteriaType = entry.getKey();
            double ratingDetailValue = entry.getValue();
            double previousTotal = criteriaAverages.getOrDefault(criteriaType, 0.0) * (newNumberOfRatings - 1);
            double newAverage = (previousTotal + ratingDetailValue) / newNumberOfRatings;
            criteriaAverages.put(criteriaType, newAverage);
        }

        ratingSummaryPort.save(ratingSummary);
    }

    private void updateRatingTrend(CreateRatingDto req) {
        var ratingTrend = ratingTrendPort.getRatingTrendByAccId(req.getAccommodationId());
        if (ratingTrend == null) {
            log.error("Rating trend not created for accommodation {}, check why?", req.getAccommodationId());
            return;
        }

        LocalDate now = LocalDate.now();
        String yearMonth = now.format(YEAR_MONTH_FORMATTER);

        updateOrCreateMonthlyRating(ratingTrend.getId(), yearMonth, req.getValue());
    }

    private void updateOrCreateMonthlyRating(Long ratingTrendId, String yearMonth, Double ratingValue) {
        var monthlyRating = monthlyRatingPort.getByRatingTrendIdAndMonth(ratingTrendId, yearMonth);

        if (monthlyRating == null) {
            monthlyRating = MonthlyRatingEntity.builder()
                    .ratingTrendId(ratingTrendId)
                    .ratingCount(1)
                    .totalRating(ratingValue.longValue())
                    .yearMonth(yearMonth)
                    .build();
        } else {
            monthlyRating.setRatingCount(monthlyRating.getRatingCount() + 1);
            monthlyRating.setTotalRating(monthlyRating.getTotalRating() + ratingValue.longValue());
        }

        monthlyRatingPort.save(monthlyRating);
    }

    private void validateRequest(CreateRatingDto req) {
        var existedRating = ratingPort.getRatingByUnitIdAndUserId(req.getUnitId(), req.getUserId());
        if (existedRating != null) {
            throw new AppException(ErrorCode.RATING_ALREADY_EXIST);
        }

        if (req.getValue() < 1 || req.getValue() > 10) {
            throw new AppException(ErrorCode.INVALID_RATING_VALUE);
        }

//        var bookingDto = bookingPort.getCompletedBookingByUserIdAndUnitId(req.getUserId(), req.getUnitId());
//        if (bookingDto == null) {
//            log.error("User {} does not have booking with unit {}", req.getUserId(), req.getUnitId());
//            throw new AppException(ErrorCode.FORBIDDEN_CREATE_RATING);
//        }
    }
}