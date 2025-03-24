package huy.project.rating_service.core.service.impl;

import huy.project.rating_service.core.domain.dto.request.CreateRatingDto;
import huy.project.rating_service.core.domain.dto.request.RatingParams;
import huy.project.rating_service.core.domain.dto.response.PageInfo;
import huy.project.rating_service.core.domain.dto.response.RatingDto;
import huy.project.rating_service.core.domain.entity.RatingEntity;
import huy.project.rating_service.core.service.IRatingService;
import huy.project.rating_service.core.usecase.CreateRatingUseCase;
import huy.project.rating_service.core.usecase.GetRatingUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingService implements IRatingService {
    private final CreateRatingUseCase createRatingUseCase;
    private final GetRatingUseCase getRatingUseCase;

    @Override
    public RatingEntity createRating(CreateRatingDto req) {
        return createRatingUseCase.createRating(req);
    }

    @Override
    public Pair<PageInfo, List<RatingDto>> getAllRatings(RatingParams params) {
        return getRatingUseCase.getAllRatings(params);
    }
}
