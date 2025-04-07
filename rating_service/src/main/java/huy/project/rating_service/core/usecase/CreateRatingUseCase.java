package huy.project.rating_service.core.usecase;

import huy.project.rating_service.core.domain.constant.ErrorCode;
import huy.project.rating_service.core.domain.dto.request.CreateRatingDto;
import huy.project.rating_service.core.domain.entity.RatingEntity;
import huy.project.rating_service.core.domain.exception.AppException;
import huy.project.rating_service.core.domain.mapper.RatingMapper;
import huy.project.rating_service.core.port.IBookingPort;
import huy.project.rating_service.core.port.IRatingPort;
import huy.project.rating_service.core.port.IRatingSummaryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateRatingUseCase {
    private final IRatingPort ratingPort;
    private final IBookingPort bookingPort;
    private final IRatingSummaryPort ratingSummaryPort;

    @Transactional(rollbackFor = Exception.class)
    public RatingEntity createRating(CreateRatingDto req) {
        validateRequest(req);

        var rating = RatingMapper.INSTANCE.toEntity(req);
        rating = ratingPort.save(rating);

        var ratingSummary = ratingSummaryPort.getRatingSummaryByAccId(req.getAccommodationId());
        if (ratingSummary == null) {
            log.error("Accommodation {} not found", req.getAccommodationId());
            throw new AppException(ErrorCode.ACCOMMODATION_NOT_FOUND);
        }
        ratingSummary.setNumberOfRatings(ratingSummary.getNumberOfRatings() + 1);
        ratingSummary.setTotalRating(ratingSummary.getTotalRating() + req.getValue().longValue());
        ratingSummaryPort.save(ratingSummary);

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
