package huy.project.rating_service.core.service.impl;

import huy.project.rating_service.core.domain.dto.CreateRatingDto;
import huy.project.rating_service.core.domain.entity.RatingEntity;
import huy.project.rating_service.core.service.IRatingService;
import huy.project.rating_service.core.usecase.CreateRatingUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RatingService implements IRatingService {
    private final CreateRatingUseCase createRatingUseCase;

    @Override
    public RatingEntity createRating(CreateRatingDto req) {
        return createRatingUseCase.createRating(req);
    }
}
