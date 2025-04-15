package huy.project.rating_service.infrastructure.repository.adapter;

import huy.project.rating_service.core.domain.constant.ErrorCode;
import huy.project.rating_service.core.domain.entity.RatingHelpfulnessEntity;
import huy.project.rating_service.core.domain.exception.AppException;
import huy.project.rating_service.core.port.IRatingHelpfulnessPort;
import huy.project.rating_service.infrastructure.repository.IRatingHelpfulnessRepository;
import huy.project.rating_service.infrastructure.repository.mapper.RatingHelpfulnessMapper;
import huy.project.rating_service.infrastructure.repository.model.RatingHelpfulnessModel;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RatingHelpfulnessAdapter implements IRatingHelpfulnessPort {
    IRatingHelpfulnessRepository ratingHelpfulnessRepository;

    @Override
    public RatingHelpfulnessEntity save(RatingHelpfulnessEntity ratingHelpfulness) {
        try {
            RatingHelpfulnessModel model = RatingHelpfulnessMapper.INSTANCE.toModel(ratingHelpfulness);
            return RatingHelpfulnessMapper.INSTANCE.toEntity(ratingHelpfulnessRepository.save(model));
        } catch (Exception e) {
            throw new AppException(ErrorCode.SAVE_RATING_HELPFUL_FAILED);
        }
    }

    @Override
    public RatingHelpfulnessEntity getByUserIdAndRatingId(Long userId, Long ratingId) {
        return RatingHelpfulnessMapper.INSTANCE.toEntity(
                ratingHelpfulnessRepository.findByUserIdAndRatingId(userId, ratingId).orElse(null)
        );
    }
}
