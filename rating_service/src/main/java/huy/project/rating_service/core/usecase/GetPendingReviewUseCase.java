package huy.project.rating_service.core.usecase;

import huy.project.rating_service.core.domain.dto.request.PendingReviewParams;
import huy.project.rating_service.core.domain.dto.response.AccThumbnailDto;
import huy.project.rating_service.core.domain.dto.response.PageInfo;
import huy.project.rating_service.core.domain.dto.response.PendingReviewDto;
import huy.project.rating_service.core.domain.entity.PendingReviewEntity;
import huy.project.rating_service.core.port.IAccommodationPort;
import huy.project.rating_service.core.port.IPendingReviewPort;
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
public class GetPendingReviewUseCase {
    private final IPendingReviewPort pendingReviewPort;
    private final IAccommodationPort accommodationPort;

    public Pair<PageInfo, List<PendingReviewDto>> getPendingReviews(PendingReviewParams params) {
        var pendingReviewPair = pendingReviewPort.getPendingReviews(params);
        var pendingReviews = pendingReviewPair.getSecond();
        if (CollectionUtils.isEmpty(pendingReviews)) {
            return Pair.of(pendingReviewPair.getFirst(), List.of());
        }

        var accommodationIds = pendingReviews.stream()
                .map(PendingReviewEntity::getAccommodationId)
                .distinct().toList();
        var accommodationMap = accommodationPort.getAccThumbnailsByIds(accommodationIds)
                .stream()
                .collect(Collectors.toMap(AccThumbnailDto::getId, Function.identity()));

        // Not implement get booking details here

        var pendingReviewDtos = pendingReviews.stream()
                .map(pr -> {
                    var dto = PendingReviewDto.builder()
                            .id(pr.getId())
                            .userId(pr.getUserId())
                            .bookingId(pr.getBookingId())
                            .unitId(pr.getUnitId())
                            .accommodationId(pr.getAccommodationId())
                            .build();
                    var accThumbnail = accommodationMap.get(pr.getAccommodationId());
                    if (accThumbnail != null) {
                        dto.setAccommodationName(accThumbnail.getName());
                        dto.setAccommodationImageUrl(accThumbnail.getThumbnailUrl());
                        dto.setUnitName(accThumbnail.getUnits()
                                .stream()
                                .filter(u -> u.getId().equals(dto.getUnitId()))
                                .findFirst()
                                .get().getName());
                    }
                    return dto;
                }).toList();

        return Pair.of(pendingReviewPair.getFirst(), pendingReviewDtos);
    }
}
