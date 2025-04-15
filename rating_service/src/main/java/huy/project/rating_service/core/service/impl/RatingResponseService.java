package huy.project.rating_service.core.service.impl;

import huy.project.rating_service.core.domain.dto.request.CreateRatingResponseDto;
import huy.project.rating_service.core.domain.entity.RatingResponseEntity;
import huy.project.rating_service.core.service.IRatingResponseService;
import huy.project.rating_service.core.usecase.CreateRatingResponseUseCase;
import huy.project.rating_service.core.usecase.GetRatingResponseUseCase;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RatingResponseService implements IRatingResponseService {
    CreateRatingResponseUseCase createRatingResponseUseCase;
    GetRatingResponseUseCase getRatingResponseUseCase;

    @Override
    public RatingResponseEntity createRatingResponse(Long userId, CreateRatingResponseDto req) {
        return createRatingResponseUseCase.createRatingResponse(userId, req);
    }

    @Override
    public List<RatingResponseEntity> getRatingResponsesByRatingIds(List<Long> ratingIds) {
        return getRatingResponseUseCase.getRatingResponsesByRatingIds(ratingIds);
    }
}
