package huy.project.rating_service.core.service;

import huy.project.rating_service.core.domain.dto.CreateRatingDto;
import huy.project.rating_service.core.domain.entity.RatingEntity;

public interface IRatingService {
    RatingEntity createRating(CreateRatingDto req);
}
