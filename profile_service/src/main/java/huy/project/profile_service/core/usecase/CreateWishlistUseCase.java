package huy.project.profile_service.core.usecase;

import huy.project.profile_service.core.domain.constant.ErrorCode;
import huy.project.profile_service.core.domain.dto.request.CreateWishlistDto;
import huy.project.profile_service.core.domain.dto.request.CreateWishlistItemDto;
import huy.project.profile_service.core.domain.entity.WishlistEntity;
import huy.project.profile_service.core.domain.entity.WishlistItemEntity;
import huy.project.profile_service.core.domain.exception.AppException;
import huy.project.profile_service.core.port.IWishlistItemPort;
import huy.project.profile_service.core.port.IWishlistPort;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CreateWishlistUseCase {
    IWishlistPort wishlistPort;
    IWishlistItemPort wishlistItemPort;

    GetTouristUseCase getTouristUseCase;

    @Transactional(rollbackFor = Exception.class)
    public WishlistEntity createWishlist(Long touristId, CreateWishlistDto req) {
        // validate tourist is existed
        getTouristUseCase.getTouristById(touristId);

        // TODO: check accommodation exist
        // then get accommodation name and thumbnail

        WishlistEntity wishlist = WishlistEntity.builder()
                .touristId(touristId)
                .name(req.getName())
                .build();
        wishlist = wishlistPort.save(wishlist);
        final Long wishlistId = wishlist.getId();

        List<Long> accIds = req.getItems().stream()
                .map(CreateWishlistItemDto::getAccommodationId)
                .distinct().toList();

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
                        .accommodationName(null)
                        .accommodationImageUrl(null)
                        .build()).toList();
        wishlist.setItems(wishlistItemPort.saveAll(newItems));

        return wishlist;
    }
}
