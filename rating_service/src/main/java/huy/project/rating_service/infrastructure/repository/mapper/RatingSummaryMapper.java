package huy.project.rating_service.infrastructure.repository.mapper;

import huy.project.rating_service.core.domain.constant.RatingCriteriaType;
import huy.project.rating_service.core.domain.entity.RatingSummaryEntity;
import huy.project.rating_service.infrastructure.repository.model.RatingSummaryModel;
import huy.project.rating_service.kernel.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class RatingSummaryMapper {
    private final JsonUtils jsonUtils;

    public RatingSummaryModel toModel(RatingSummaryEntity entity) {
        if (entity == null) {
            return null;
        }

        return RatingSummaryModel.builder()
                .id(entity.getId())
                .accommodationId(entity.getAccommodationId())
                .numberOfRatings(entity.getNumberOfRatings())
                .totalRating(entity.getTotalRating())
                .isSyncedWithSearchService(entity.getIsSyncedWithSearchService())
                .distribution(jsonUtils.toJson(entity.getDistribution()))
                .criteriaAverages(jsonUtils.toJson(entity.getCriteriaAverages()))
                .build();
    }

    public RatingSummaryEntity toEntity(RatingSummaryModel model) {
        if (model == null) {
            return null;
        }
        return RatingSummaryEntity.builder()
                .id(model.getId())
                .accommodationId(model.getAccommodationId())
                .numberOfRatings(model.getNumberOfRatings())
                .totalRating(model.getTotalRating())
                .isSyncedWithSearchService(model.getIsSyncedWithSearchService())
                .distribution(jsonUtils.fromJson(model.getDistribution(), HashMap.class, Integer.class, Integer.class))
                .criteriaAverages(jsonUtils.fromJson(model.getCriteriaAverages(), HashMap.class, RatingCriteriaType.class, Double.class))
                .build();
    }
}
