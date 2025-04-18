package huy.project.rating_service.core.usecase;

import huy.project.rating_service.core.domain.constant.ErrorCode;
import huy.project.rating_service.core.domain.dto.request.CreateRatingResponseDto;
import huy.project.rating_service.core.domain.entity.RatingResponseEntity;
import huy.project.rating_service.core.domain.exception.AppException;
import huy.project.rating_service.core.domain.mapper.RatingMapper;
import huy.project.rating_service.core.port.IAccommodationPort;
import huy.project.rating_service.core.port.IRatingResponsePort;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CreateRatingResponseUseCase {
    IRatingResponsePort ratingResponsePort;
    GetRatingUseCase getRatingUseCase;
    IAccommodationPort accommodationPort;

    @Transactional(rollbackFor = Exception.class)
    public RatingResponseEntity createRatingResponse(Long userId, CreateRatingResponseDto req) {
        // check if rating exists
        var rating = getRatingUseCase.getRatingById(req.getRatingId());

        // check if user is the owner of accommodation
        var accommodation = accommodationPort.getAccById(rating.getAccommodationId());
        if (accommodation == null) {
            log.error("Accommodation {} not found, check why?", rating.getAccommodationId());
            throw new AppException(ErrorCode.ACCOMMODATION_NOT_FOUND);
        }

        if (!accommodation.getHostId().equals(userId)) {
            log.error("User {} is not the owner of accommodation {}", userId, accommodation.getId());
            throw new AppException(ErrorCode.FORBIDDEN_CREATE_RATING_RESPONSE);
        }

        var ratingResponse = RatingMapper.INSTANCE.toRatingResponseEntity(userId, req);
        return ratingResponsePort.save(ratingResponse);
    }
}
