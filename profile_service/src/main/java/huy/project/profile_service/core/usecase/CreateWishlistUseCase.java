package huy.project.profile_service.core.usecase;

import huy.project.profile_service.core.domain.constant.ErrorCode;
import huy.project.profile_service.core.domain.dto.request.CreateWishlistDto;
import huy.project.profile_service.core.domain.dto.request.CreateWishlistItemDto;
import huy.project.profile_service.core.domain.dto.response.AccommodationDto;
import huy.project.profile_service.core.domain.entity.WishlistEntity;
import huy.project.profile_service.core.domain.entity.WishlistItemEntity;
import huy.project.profile_service.core.domain.exception.AppException;
import huy.project.profile_service.core.port.IWishlistItemPort;
import huy.project.profile_service.core.port.IWishlistPort;
import huy.project.profile_service.core.port.client.IAccommodationPort;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CreateWishlistUseCase {
    IWishlistPort wishlistPort;
    IWishlistItemPort wishlistItemPort;
    IAccommodationPort accommodationPort;

    GetTouristUseCase getTouristUseCase;

    @Transactional(rollbackFor = Exception.class)
    public WishlistEntity createWishlist(Long touristId, CreateWishlistDto req) {
        // validate tourist is existed
        getTouristUseCase.getTouristById(touristId);

        //check accommodation exist
        List<Long> accIds = req.getItems().stream()
                .map(CreateWishlistItemDto::getAccommodationId).distinct().toList();
        Map<Long, AccommodationDto> accommodationMap;
        if (!CollectionUtils.isEmpty(accIds)) {
            List<AccommodationDto> accommodations = accommodationPort.getAccommodations(accIds);
            accIds = accommodations.stream()
                    .map(AccommodationDto::getId).distinct().toList();
            accommodationMap = accommodations.stream()
                    .collect(Collectors.toMap(AccommodationDto::getId, Function.identity()));
        } else {
            accommodationMap = null;
        }

        WishlistEntity wishlist = WishlistEntity.builder()
                .touristId(touristId)
                .name(req.getName())
                .build();
        wishlist = wishlistPort.save(wishlist);
        final Long wishlistId = wishlist.getId();

        if (accIds.isEmpty()) {
            return wishlist;
        }

        List<WishlistItemEntity> existedItems = wishlistItemPort.getWishlistByAccIdsAndWishlistId(accIds, wishlist.getId());
        if (!CollectionUtils.isEmpty(existedItems)) {
            log.error("some wishlist item already exists");
            throw new AppException(ErrorCode.WISHLIST_ITEM_ALREADY_EXISTS);
        }

        List<WishlistItemEntity> newItems = accIds.stream()
                .map(accId -> WishlistItemEntity.builder()
                        .wishlistId(wishlistId)
                        .accommodationId(accId)
                        .accommodationName(accommodationMap.get(accId).getName())
                        .accommodationImageUrl(accommodationMap.get(accId).getThumbnailUrl())
                        .build()).toList();
        wishlist.setItems(wishlistItemPort.saveAll(newItems));

        return wishlist;
    }
}
