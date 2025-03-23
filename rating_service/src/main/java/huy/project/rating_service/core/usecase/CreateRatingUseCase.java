package huy.project.rating_service.core.usecase;

import huy.project.rating_service.core.domain.constant.ErrorCode;
import huy.project.rating_service.core.domain.dto.CreateRatingDto;
import huy.project.rating_service.core.domain.entity.RatingEntity;
import huy.project.rating_service.core.domain.exception.exception.AppException;
import huy.project.rating_service.core.domain.mapper.RatingMapper;
import huy.project.rating_service.core.port.IBookingPort;
import huy.project.rating_service.core.port.IRatingPort;
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

    @Transactional(rollbackFor = Exception.class)
    public RatingEntity createRating(CreateRatingDto req) {
        validateRequest(req);

        var rating = RatingMapper.INSTANCE.toEntity(req);
        return ratingPort.save(rating);
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
