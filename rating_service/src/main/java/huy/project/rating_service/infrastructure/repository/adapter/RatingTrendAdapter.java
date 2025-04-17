package huy.project.rating_service.infrastructure.repository.adapter;

import huy.project.rating_service.core.domain.constant.ErrorCode;
import huy.project.rating_service.core.domain.entity.RatingTrendEntity;
import huy.project.rating_service.core.domain.exception.AppException;
import huy.project.rating_service.core.port.IRatingTrendPort;
import huy.project.rating_service.infrastructure.repository.IRatingTrendRepository;
import huy.project.rating_service.infrastructure.repository.mapper.RatingTrendMapper;
import huy.project.rating_service.infrastructure.repository.model.RatingTrendModel;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RatingTrendAdapter implements IRatingTrendPort {
    IRatingTrendRepository ratingTrendRepository;

    @Override
    public void save(RatingTrendEntity ratingTrend) {
        try {
            RatingTrendModel model = RatingTrendMapper.INSTANCE.toModel(ratingTrend);
            ratingTrendRepository.save(model);
        } catch (Exception e) {
            log.error("Error while saving rating trend: {}", e.getMessage());
            throw new AppException(ErrorCode.SAVE_RATING_TREND_FAILED);
        }
    }

    @Override
    public RatingTrendEntity getRatingTrendByAccId(Long accId) {
        return RatingTrendMapper.INSTANCE.toEntity(
                ratingTrendRepository.findByAccommodationId(accId).orElse(null)
        );
    }
}
