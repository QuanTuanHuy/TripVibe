package huy.project.rating_service.core.usecase;

import huy.project.rating_service.core.domain.constant.ErrorCode;
import huy.project.rating_service.core.domain.dto.request.CreateRatingDto;
import huy.project.rating_service.core.domain.entity.MonthlyRatingEntity;
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

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CreateRatingUseCase {
    IRatingPort ratingPort;
    IBookingPort bookingPort;
    IRatingSummaryPort ratingSummaryPort;
    IRatingTrendPort ratingTrendPort;
    IMonthlyRatingPort monthlyRatingPort;


    @Transactional(rollbackFor = Exception.class)
    public RatingEntity createRating(CreateRatingDto req) {
        validateRequest(req);

        var rating = RatingMapper.INSTANCE.toEntity(req);
        rating.setNumberOfHelpful(0);
        rating.setNumberOfUnhelpful(0);
        rating = ratingPort.save(rating);

        // update rating summary
        var ratingSummary = ratingSummaryPort.getRatingSummaryByAccId(req.getAccommodationId());
        if (ratingSummary == null) {
            log.error("Accommodation {} not found", req.getAccommodationId());
            throw new AppException(ErrorCode.ACCOMMODATION_NOT_FOUND);
        }
        ratingSummary.setNumberOfRatings(ratingSummary.getNumberOfRatings() + 1);
        ratingSummary.setTotalRating(ratingSummary.getTotalRating() + req.getValue().longValue());
        ratingSummary.setIsSyncedWithSearchService(false);
        ratingSummaryPort.save(ratingSummary);

        // update rating trend
        var ratingTrend = ratingTrendPort.getRatingTrendByAccId(req.getAccommodationId());
        if (ratingTrend == null) {
            log.error("rating trend not created for accommodation {}, check why?", req.getAccommodationId());
        } else {
            LocalDate now = LocalDate.now();
            String yearMonth = now.getYear() + "-" + now.getMonthValue();
            log.info("yearMonth: {}", yearMonth);

            var monthlyRating = monthlyRatingPort.getByRatingTrendIdAndMonth(ratingTrend.getId(), yearMonth);
            if (monthlyRating == null) {
                monthlyRating = MonthlyRatingEntity.builder()
                        .ratingTrendId(ratingTrend.getId())
                        .ratingCount(0)
                        .totalRating(0L)
                        .yearMonth(yearMonth)
                        .build();
            }
            monthlyRating.setRatingCount(monthlyRating.getRatingCount() + 1);
            monthlyRating.setTotalRating(monthlyRating.getTotalRating() + req.getValue().longValue());

            monthlyRatingPort.save(monthlyRating);
        }

        return rating;
    }

    private void validateRequest(CreateRatingDto req) {
        var existedRating = ratingPort.getRatingByUnitIdAndUserId(req.getUnitId(), req.getUserId());
        if (existedRating != null) {
            throw new AppException(ErrorCode.RATING_ALREADY_EXIST);
        }

        if (req.getValue() < 1 || req.getValue() > 10) {
            throw new AppException(ErrorCode.INVALID_RATING_VALUE);
        }

        var bookingDto = bookingPort.getCompletedBookingByUserIdAndUnitId(req.getUserId(), req.getUnitId());
        if (bookingDto == null) {
            log.error("user {} does not have booking with unit {}", req.getUserId(), req.getUnitId());
            throw new AppException(ErrorCode.FORBIDDEN_CREATE_RATING);
        }
    }
}
