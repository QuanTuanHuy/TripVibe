package huy.project.rating_service.core.usecase;

import huy.project.rating_service.core.domain.constant.ErrorCode;
import huy.project.rating_service.core.domain.dto.request.MyRatingParams;
import huy.project.rating_service.core.domain.dto.request.RatingParams;
import huy.project.rating_service.core.domain.dto.response.*;
import huy.project.rating_service.core.domain.entity.RatingEntity;
import huy.project.rating_service.core.domain.exception.AppException;
import huy.project.rating_service.core.domain.mapper.RatingMapper;
import huy.project.rating_service.core.port.IRatingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetRatingUseCase {
    private final IRatingPort ratingPort;
    private final GetAccommodationUseCase getAccommodationUseCase;
    private final GetRatingResponseUseCase getRatingResponseUseCase;
    private final GetUserProfileUseCase getUserProfileUseCase;

    public Pair<PageInfo, List<RatingDto>> getAllRatings(RatingParams params) {
        var result = ratingPort.getAllRatings(params);
        var ratingEntities = result.getSecond();
        if (CollectionUtils.isEmpty(ratingEntities)) {
            return Pair.of(result.getFirst(), List.of());
        }

        var ratingDtoList = ratingEntities.stream().map(RatingMapper.INSTANCE::toDto).toList();

        List<Long> userIds = ratingEntities.stream().map(RatingEntity::getUserId).distinct().toList();
        var userProfileMap = getUserProfileUseCase.getUserProfilesByIds(userIds).stream()
                .collect(Collectors.toMap(UserProfileDto::getUserId, Function.identity()));

        List<Long> unitIds = ratingEntities.stream().map(RatingEntity::getUnitId).distinct().toList();
        var unitMap = getAccommodationUseCase.getUnitsByIds(unitIds).stream()
                .collect(Collectors.toMap(UnitDto::getId, Function.identity()));

        var ratingEntityMap = ratingEntities.stream()
                .collect(Collectors.toMap(RatingEntity::getId, Function.identity()));

        List<Long> ratingIds = ratingEntities.stream()
                .map(RatingEntity::getId).distinct().toList();
        var ratingResponseMap = getRatingResponseUseCase.getRatingResponsesByRatingIds(ratingIds)
                .stream()
                .map(RatingMapper.INSTANCE::toResponseDto)
                .collect(Collectors.toMap(RatingResponseDto::getRatingId, Function.identity()));

        ratingDtoList.forEach(rating -> {
            var ratingEntity = ratingEntityMap.get(rating.getId());
            var userProfile = userProfileMap.get(ratingEntity.getUserId());
            var unit = unitMap.get(ratingEntity.getUnitId());
            rating.setUser(userProfile);
            rating.setUnit(unit);
            rating.setRatingResponse(ratingResponseMap.get(rating.getId()));
        });

        return Pair.of(result.getFirst(), ratingDtoList);
    }

    public Pair<PageInfo, List<RatingDto>> getRatingsByUserId(MyRatingParams params) {
        var newParams = RatingParams.builder()
                .page(params.getPage())
                .pageSize(params.getPageSize())
                .sortBy(params.getSortBy())
                .sortType(params.getSortType())
                .userId(params.getUserId())
                .build();
        return getAllRatings(newParams);
    }

    public RatingEntity getRatingById(Long id) {
        var rating = ratingPort.getRatingById(id);
        if (rating == null) {
            log.error("Rating {} not found", id);
            throw new AppException(ErrorCode.RATING_NOT_FOUND);
        }
        return rating;
    }
}
