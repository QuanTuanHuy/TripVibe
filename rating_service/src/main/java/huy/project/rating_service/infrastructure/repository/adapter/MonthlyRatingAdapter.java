package huy.project.rating_service.infrastructure.repository.adapter;

import huy.project.rating_service.core.domain.constant.ErrorCode;
import huy.project.rating_service.core.domain.entity.MonthlyRatingEntity;
import huy.project.rating_service.core.domain.exception.AppException;
import huy.project.rating_service.core.port.IMonthlyRatingPort;
import huy.project.rating_service.infrastructure.repository.IMonthlyRatingRepository;
import huy.project.rating_service.infrastructure.repository.mapper.MonthlyRatingMapper;
import huy.project.rating_service.infrastructure.repository.model.MonthlyRatingModel;
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
public class MonthlyRatingAdapter implements IMonthlyRatingPort {
    IMonthlyRatingRepository monthlyRatingRepository;

    @Override
    public void save(MonthlyRatingEntity monthlyRating) {
        try {
            MonthlyRatingModel model = MonthlyRatingMapper.INSTANCE.toModel(monthlyRating);
            monthlyRatingRepository.save(model);
        } catch (Exception e) {
            log.error("Error while saving monthly rating: {}", e.getMessage());
            throw new AppException(ErrorCode.SAVE_MONTHLY_RATING_FAILED);
        }
    }

    @Override
    public List<MonthlyRatingEntity> getByRatingTrendId(Long ratingTrendId) {
        return MonthlyRatingMapper.INSTANCE.toListEntity(monthlyRatingRepository.findByRatingTrendId(ratingTrendId));
    }

    @Override
    public MonthlyRatingEntity getByRatingTrendIdAndMonth(Long ratingTrendId, String month) {
        return MonthlyRatingMapper.INSTANCE.toEntity(monthlyRatingRepository
                .findByRatingTrendIdAndYearMonth(ratingTrendId, month).orElse(null));
    }
}
