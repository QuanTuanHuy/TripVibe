package huy.project.rating_service.core.service;

import huy.project.rating_service.core.domain.dto.request.CreateRatingDto;
import huy.project.rating_service.core.domain.dto.request.RatingParams;
import huy.project.rating_service.core.domain.dto.response.PageInfo;
import huy.project.rating_service.core.domain.dto.response.RatingDto;
import huy.project.rating_service.core.domain.entity.RatingEntity;
import org.springframework.data.util.Pair;

import java.util.List;

public interface IRatingService {
    RatingEntity createRating(CreateRatingDto req);
    Pair<PageInfo, List<RatingDto>> getAllRatings(RatingParams params);
}
