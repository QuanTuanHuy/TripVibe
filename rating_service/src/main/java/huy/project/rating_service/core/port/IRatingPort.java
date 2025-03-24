package huy.project.rating_service.core.port;

import huy.project.rating_service.core.domain.dto.request.RatingParams;
import huy.project.rating_service.core.domain.dto.response.PageInfo;
import huy.project.rating_service.core.domain.entity.RatingEntity;
import org.springframework.data.util.Pair;

import java.util.List;

public interface IRatingPort {
    RatingEntity save(RatingEntity rating);
    RatingEntity getRatingByUnitIdAndUserId(Long unitId, Long userId);
    Pair<PageInfo, List<RatingEntity>> getAllRatings(RatingParams params);
}
