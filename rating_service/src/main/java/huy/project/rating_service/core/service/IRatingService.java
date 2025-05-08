package huy.project.rating_service.core.service;

import huy.project.rating_service.core.domain.dto.request.CreateRatingDto;
import huy.project.rating_service.core.domain.dto.request.CreateRatingHelpfulnessDto;
import huy.project.rating_service.core.domain.dto.request.RatingParams;
import huy.project.rating_service.core.domain.dto.response.PageInfo;
import huy.project.rating_service.core.domain.dto.response.RatingDto;
import huy.project.rating_service.core.domain.dto.response.RatingStatisticDto;
import huy.project.rating_service.core.domain.entity.RatingEntity;
import huy.project.rating_service.core.domain.entity.RatingHelpfulnessEntity;
import org.springframework.data.util.Pair;

import java.util.List;

public interface IRatingService {
    RatingEntity createRating(CreateRatingDto req);
    Pair<PageInfo, List<RatingDto>> getAllRatings(RatingParams params);
    RatingHelpfulnessEntity createRatingHelpfulness(Long userId, CreateRatingHelpfulnessDto req);
    RatingStatisticDto getStatisticByAccId(Long accommodationId);
}
