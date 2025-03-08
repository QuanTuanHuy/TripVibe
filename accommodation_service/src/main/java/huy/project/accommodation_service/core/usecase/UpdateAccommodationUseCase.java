package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.domain.dto.request.CreateAccommodationAmenityDto;
import huy.project.accommodation_service.core.domain.dto.request.UpdateAccAmenityDto;
import huy.project.accommodation_service.core.domain.entity.AccommodationAmenityEntity;
import huy.project.accommodation_service.core.exception.AppException;
import huy.project.accommodation_service.core.port.IAccommodationAmenityPort;
import huy.project.accommodation_service.core.validation.AccommodationValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateAccommodationUseCase {
    private final GetAccommodationAmenityUseCase getAccAmenityUseCase;

    private final IAccommodationAmenityPort accAmenityPort;

    private final AccommodationValidation accValidation;

    @Transactional(rollbackFor = Exception.class)
    public void updateAccAmenity(Long userId, Long accId, UpdateAccAmenityDto req) {
        if (!accValidation.accommodationExistToHost(userId, accId)) {
            log.error("Accommodation with id {} not found or not belong to user {}", accId, userId);
            throw new AppException(ErrorCode.ACCOMMODATION_NOT_FOUND);
        }

        List<AccommodationAmenityEntity> existedAccAmenities = getAccAmenityUseCase.getAccAmenitiesByAccId(accId);
        var existedAmenityIds = existedAccAmenities.stream()
                .map(AccommodationAmenityEntity::getAmenityId).collect(Collectors.toSet());

        // delete amenities
        List<Long> allDeleteAmenityIds = new ArrayList<>(req.getDeleteAmenityIds());
        if (!existedAmenityIds.containsAll(req.getDeleteAmenityIds())) {
            log.error("Some amenity ids not found in accommodation {}", accId);
            throw new AppException(ErrorCode.ACCOMMODATION_AMENITY_NOT_FOUND);
        }

        // delete amenities for update
        allDeleteAmenityIds.addAll(req.getNewAmenities().stream()
                .map(CreateAccommodationAmenityDto::getAmenityId)
                .filter(existedAmenityIds::contains)
                .toList());
        if (!CollectionUtils.isEmpty(allDeleteAmenityIds)) {
            accAmenityPort.deleteByAccIdAndAmenities(accId, allDeleteAmenityIds);
        }

        // save new amenities
        var newAmenities = req.getNewAmenities().stream()
                .map(dto -> AccommodationAmenityEntity.builder()
                        .accommodationId(accId)
                        .amenityId(dto.getAmenityId())
                        .fee(dto.getFee())
                        .needToReserve(dto.getNeedToReserve())
                        .build())
                .toList();
        if (!CollectionUtils.isEmpty(newAmenities)) {
            accAmenityPort.saveAll(newAmenities);
        }
    }
}
