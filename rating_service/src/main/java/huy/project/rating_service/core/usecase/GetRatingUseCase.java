package huy.project.rating_service.core.usecase;

import huy.project.rating_service.core.domain.constant.ErrorCode;
import huy.project.rating_service.core.domain.dto.request.RatingParams;
import huy.project.rating_service.core.domain.dto.response.PageInfo;
import huy.project.rating_service.core.domain.dto.response.RatingDto;
import huy.project.rating_service.core.domain.dto.response.UnitDto;
import huy.project.rating_service.core.domain.dto.response.UserProfileDto;
import huy.project.rating_service.core.domain.entity.RatingEntity;
import huy.project.rating_service.core.domain.exception.AppException;
import huy.project.rating_service.core.domain.mapper.RatingMapper;
import huy.project.rating_service.core.port.IAccommodationPort;
import huy.project.rating_service.core.port.IRatingPort;
import huy.project.rating_service.core.port.IUserProfilePort;
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
    private final IUserProfilePort userProfilePort;
    private final IAccommodationPort accommodationPort;

    public Pair<PageInfo, List<RatingDto>> getAllRatings(RatingParams params) {
        var result = ratingPort.getAllRatings(params);
        var ratingEntities = result.getSecond();
        if (CollectionUtils.isEmpty(ratingEntities)) {
            return Pair.of(result.getFirst(), List.of());
        }

        var ratingDtoList = ratingEntities.stream().map(RatingMapper.INSTANCE::toDto).toList();

        List<Long> userIds = ratingEntities.stream().map(RatingEntity::getUserId).toList();
        var userProfileMap = userProfilePort.getUserProfilesByIds(userIds).stream()
                .collect(Collectors.toMap(UserProfileDto::getUserId, Function.identity()));

        List<Long> unitIds = ratingEntities.stream().map(RatingEntity::getUnitId).toList();
        var unitMap = accommodationPort.getUnitsByIds(unitIds).stream()
                .collect(Collectors.toMap(UnitDto::getId, Function.identity()));

        var ratingEntityMap = ratingEntities.stream()
                .collect(Collectors.toMap(RatingEntity::getId, Function.identity()));

        ratingDtoList.forEach(rating -> {
            var ratingEntity = ratingEntityMap.get(rating.getId());
            var userProfile = userProfileMap.get(ratingEntity.getUserId());
            var unit = unitMap.get(ratingEntity.getUnitId());
            rating.setUser(userProfile);
            rating.setUnit(unit);
        });

        return Pair.of(result.getFirst(), ratingDtoList);
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
