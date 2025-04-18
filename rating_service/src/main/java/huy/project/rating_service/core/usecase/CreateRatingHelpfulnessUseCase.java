package huy.project.rating_service.core.usecase;

import huy.project.rating_service.core.domain.dto.request.CreateRatingHelpfulnessDto;
import huy.project.rating_service.core.domain.entity.RatingHelpfulnessEntity;
import huy.project.rating_service.core.port.IRatingHelpfulnessPort;
import huy.project.rating_service.core.port.IRatingPort;
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
public class CreateRatingHelpfulnessUseCase {
    IRatingHelpfulnessPort ratingHelpfulnessPort;
    IRatingPort ratingPort;

    @Transactional(rollbackFor = Exception.class)
    public RatingHelpfulnessEntity createRatingHelpfulness(Long userId, CreateRatingHelpfulnessDto req) {
        var rating = ratingPort.getRatingById(req.getRatingId());

        var existedHelpfulness = ratingHelpfulnessPort.getByUserIdAndRatingId(userId, req.getRatingId());
        if (existedHelpfulness != null) {
            if (existedHelpfulness.getIsHelpful() == req.getIsHelpful()) {
                log.error("User {} has already rated this rating as {}", userId, req.getIsHelpful());
                return existedHelpfulness;
            } else {
                existedHelpfulness.setIsHelpful(req.getIsHelpful());

                if (existedHelpfulness.getIsHelpful()) {
                    rating.setNumberOfHelpful(rating.getNumberOfHelpful() + 1);
                    rating.setNumberOfUnhelpful(rating.getNumberOfUnhelpful() - 1);
                } else {
                    rating.setNumberOfHelpful(rating.getNumberOfUnhelpful() + 1);
                    rating.setNumberOfUnhelpful(rating.getNumberOfHelpful() - 1);
                }
                ratingPort.save(rating);

                return ratingHelpfulnessPort.save(existedHelpfulness);
            }
        } else {
            var ratingHelpfulness = RatingHelpfulnessEntity.builder()
                    .ratingId(req.getRatingId())
                    .userId(userId)
                    .isHelpful(req.getIsHelpful())
                    .build();

            if (req.getIsHelpful()) {
                rating.setNumberOfHelpful(rating.getNumberOfHelpful() + 1);
            } else {
                rating.setNumberOfUnhelpful(rating.getNumberOfUnhelpful() + 1);
            }
            ratingPort.save(rating);

            return ratingHelpfulnessPort.save(ratingHelpfulness);
        }
    }
}
