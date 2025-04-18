package huy.project.rating_service.core.service;

import huy.project.rating_service.core.domain.dto.request.CreateRatingResponseDto;
import huy.project.rating_service.core.domain.entity.RatingResponseEntity;

import java.util.List;

public interface IRatingResponseService {
    RatingResponseEntity createRatingResponse(Long userId, CreateRatingResponseDto req);
    List<RatingResponseEntity> getRatingResponsesByRatingIds(List<Long> ratingIds);
}
