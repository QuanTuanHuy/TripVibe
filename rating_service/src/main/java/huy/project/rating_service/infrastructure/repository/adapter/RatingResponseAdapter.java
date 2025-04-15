package huy.project.rating_service.infrastructure.repository.adapter;

import huy.project.rating_service.core.domain.constant.ErrorCode;
import huy.project.rating_service.core.domain.entity.RatingResponseEntity;
import huy.project.rating_service.core.domain.exception.AppException;
import huy.project.rating_service.core.port.IRatingResponsePort;
import huy.project.rating_service.infrastructure.repository.IRatingResponseRepository;
import huy.project.rating_service.infrastructure.repository.mapper.RatingResponseMapper;
import huy.project.rating_service.infrastructure.repository.model.RatingResponseModel;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RatingResponseAdapter implements IRatingResponsePort {
    IRatingResponseRepository ratingResponseRepository;

    @Override
    public RatingResponseEntity save(RatingResponseEntity ratingResponse) {
        try {
            RatingResponseModel model = RatingResponseMapper.INSTANCE.toModel(ratingResponse);
            return RatingResponseMapper.INSTANCE.toEntity(ratingResponseRepository.save(model));
        } catch (Exception e) {
            log.error("Error when saving rating response: {}", e.getMessage());
            throw new AppException(ErrorCode.SAVE_RATING_RESPONSE_FAILED);
        }
    }

    @Override
    public List<RatingResponseEntity> getRatingResponsesByRatingIds(List<Long> ratingIds) {
        return RatingResponseMapper.INSTANCE.toListEntity(
                ratingResponseRepository.findByRatingIdIn(ratingIds));
    }
}
