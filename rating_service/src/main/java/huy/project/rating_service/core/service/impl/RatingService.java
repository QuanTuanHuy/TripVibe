package huy.project.rating_service.core.service.impl;

import huy.project.rating_service.core.domain.dto.request.CreateRatingDto;
import huy.project.rating_service.core.domain.dto.request.CreateRatingHelpfulnessDto;
import huy.project.rating_service.core.domain.dto.request.MyRatingParams;
import huy.project.rating_service.core.domain.dto.request.RatingParams;
import huy.project.rating_service.core.domain.dto.response.PageInfo;
import huy.project.rating_service.core.domain.dto.response.RatingDto;
import huy.project.rating_service.core.domain.dto.response.RatingStatisticDto;
import huy.project.rating_service.core.domain.entity.RatingEntity;
import huy.project.rating_service.core.domain.entity.RatingHelpfulnessEntity;
import huy.project.rating_service.core.service.IRatingService;
import huy.project.rating_service.core.usecase.CreateRatingHelpfulnessUseCase;
import huy.project.rating_service.core.usecase.CreateRatingUseCase;
import huy.project.rating_service.core.usecase.GetRatingStatisticUseCase;
import huy.project.rating_service.core.usecase.GetRatingUseCase;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RatingService implements IRatingService {
    CreateRatingUseCase createRatingUseCase;
    GetRatingUseCase getRatingUseCase;
    CreateRatingHelpfulnessUseCase createRatingHelpfulnessUseCase;
    GetRatingStatisticUseCase getRatingStatisticUseCase;

    @Override
    public RatingEntity createRating(CreateRatingDto req) {
        return createRatingUseCase.createRating(req);
    }

    @Override
    public Pair<PageInfo, List<RatingDto>> getRatingsByUserId(MyRatingParams params) {
        return getRatingUseCase.getRatingsByUserId(params);
    }

    @Override
    public Pair<PageInfo, List<RatingDto>> getAllRatings(RatingParams params) {
        return getRatingUseCase.getAllRatings(params);
    }

    @Override
    public RatingHelpfulnessEntity createRatingHelpfulness(Long userId, CreateRatingHelpfulnessDto req) {
        return createRatingHelpfulnessUseCase.createRatingHelpfulness(userId, req);
    }

    @Override
    public RatingStatisticDto getStatisticByAccId(Long accommodationId) {
        return getRatingStatisticUseCase.getStatisticByAccId(accommodationId);
    }
}
