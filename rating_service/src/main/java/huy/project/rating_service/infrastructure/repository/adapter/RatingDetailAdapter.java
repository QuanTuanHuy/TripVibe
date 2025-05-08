package huy.project.rating_service.infrastructure.repository.adapter;

import huy.project.rating_service.core.domain.constant.ErrorCode;
import huy.project.rating_service.core.domain.constant.RatingCriteriaType;
import huy.project.rating_service.core.domain.entity.RatingDetailEntity;
import huy.project.rating_service.core.domain.exception.AppException;
import huy.project.rating_service.core.port.IRatingDetailPort;
import huy.project.rating_service.infrastructure.repository.IRatingDetailRepository;
import huy.project.rating_service.infrastructure.repository.mapper.RatingDetailMapper;
import huy.project.rating_service.infrastructure.repository.model.RatingDetailModel;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RatingDetailAdapter implements IRatingDetailPort {
    IRatingDetailRepository ratingDetailRepository;

    @Override
    public List<RatingDetailEntity> saveAll(List<RatingDetailEntity> ratingDetails) {
        try {
            List<RatingDetailModel> models = RatingDetailMapper.INSTANCE.toListModel(ratingDetails);
            return RatingDetailMapper.INSTANCE.toListEntity(ratingDetailRepository.saveAll(models));
        } catch (Exception e) {
            log.error("Error while saving rating details: {}", e.getMessage());
            throw new AppException(ErrorCode.SAVE_RATING_DETAIL_FAILED);
        }
    }

    @Override
    public List<RatingDetailEntity> getByRatingId(Long ratingId) {
        return RatingDetailMapper.INSTANCE.toListEntity(ratingDetailRepository.findByRatingId(ratingId));
    }

    @Override
    public void deleteByRatingId(Long ratingId) {
        ratingDetailRepository.deleteByRatingId(ratingId);
    }

    @Override
    public Map<RatingCriteriaType, Double> getAverageRatingsByCriteriaForAccommodation(Long accommodationId) {
        List<Object[]> results = ratingDetailRepository.getAverageRatingsByCriteriaForAccommodation(accommodationId);
        Map<RatingCriteriaType, Double> averageRatings = new HashMap<>();

        for (Object[] result : results) {
            RatingCriteriaType criteriaType = (RatingCriteriaType) result[0];
            Double averageValue = (Double) result[1];
            averageRatings.put(criteriaType, averageValue);
        }

        return averageRatings;
    }
}
