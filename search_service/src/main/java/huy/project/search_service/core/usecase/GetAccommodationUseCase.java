package huy.project.search_service.core.usecase;

import huy.project.search_service.core.domain.constant.ErrorCode;
import huy.project.search_service.core.domain.dto.request.AccommodationParams;
import huy.project.search_service.core.domain.dto.request.AccommodationThumbnailParams;
import huy.project.search_service.core.domain.dto.response.AccommodationThumbnail;
import huy.project.search_service.core.domain.dto.response.PageInfo;
import huy.project.search_service.core.domain.entity.AccommodationEntity;
import huy.project.search_service.core.domain.exception.AppException;
import huy.project.search_service.core.port.IAccommodationPort;
import huy.project.search_service.core.port.client.IAccommodationClientPort;
import huy.project.search_service.kernel.utils.DateUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GetAccommodationUseCase {
    IAccommodationPort accommodationPort;
    IAccommodationClientPort accommodationClientPort;

    public AccommodationEntity getAccById(Long id) {
        var accommodation = accommodationPort.getAccById(id);
        if (accommodation == null) {
            log.error("Accommodation with id {} not found", id);
            throw new AppException(ErrorCode.ACCOMMODATION_NOT_FOUND);
        }
        return accommodation;
    }

    public Pair<PageInfo, List<AccommodationEntity>> getAccommodations(AccommodationParams params) {
        return accommodationPort.getAccommodations(params);
    }

    public Pair<PageInfo, List<AccommodationThumbnail>> getAccommodationsThumbnail(AccommodationParams params) {
        var result = accommodationPort.getAccommodations(params);
        var accommodations = result.getSecond();

        if (CollectionUtils.isEmpty(accommodations)) {
            return Pair.of(result.getFirst(), List.of());
        }

        var newParams = AccommodationThumbnailParams.builder()
                .ids(accommodations.stream().map(AccommodationEntity::getId).toList())
                .startDate(DateUtils.dateToLocalDate(params.getStartDate()))
                .endDate(DateUtils.dateToLocalDate(params.getEndDate()))
                .guestCount(params.getNumAdults() != null ? params.getNumAdults() :
                        (params.getNumChildren() != null ? params.getNumChildren() : 0))
                .build();
        var accommodationThumbnails = accommodationClientPort.getAccommodations(newParams);

        return Pair.of(result.getFirst(), accommodationThumbnails);
    }
}
