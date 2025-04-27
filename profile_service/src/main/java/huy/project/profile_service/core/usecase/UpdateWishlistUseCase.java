package huy.project.profile_service.core.usecase;

import huy.project.profile_service.core.domain.constant.ErrorCode;
import huy.project.profile_service.core.domain.dto.request.CreateWishlistItemDto;
import huy.project.profile_service.core.domain.entity.WishlistEntity;
import huy.project.profile_service.core.domain.entity.WishlistItemEntity;
import huy.project.profile_service.core.domain.exception.AppException;
import huy.project.profile_service.core.port.IWishlistItemPort;
import huy.project.profile_service.core.port.client.IAccommodationPort;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UpdateWishlistUseCase {
    IWishlistItemPort wishlistItemPort;
    IAccommodationPort accommodationPort;

    GetWishlistUseCase getWishlistUseCase;

    @Transactional(rollbackFor = Exception.class)
    public void addWishlistItem(Long touristId, Long wishlistId, CreateWishlistItemDto req) {
        WishlistEntity wishlist = getWishlistUseCase.getDetailWishlist(touristId, wishlistId);

        Optional<WishlistItemEntity> existingItem = wishlist.getItems().stream()
                .filter(item -> item.getAccommodationId().equals(req.getAccommodationId()))
                .findFirst();

        if (existingItem.isPresent()) {
            log.error("Accommodation with id {} already exists in wishlist {}", req.getAccommodationId(), wishlistId);
            throw new AppException(ErrorCode.WISHLIST_ITEM_ALREADY_EXISTS);
        }

        var accommodation = accommodationPort.getAccommodations(List.of(req.getAccommodationId()));
        if (CollectionUtils.isEmpty(accommodation)) {
            log.error("Accommodation with id {} not found", req.getAccommodationId());
            throw new AppException(ErrorCode.ACCOMMODATION_NOT_FOUND);
        }

        wishlistItemPort.save(WishlistItemEntity.builder()
                        .wishlistId(wishlistId)
                        .accommodationName(accommodation.get(0).getName())
                        .accommodationImageUrl(accommodation.get(0).getThumbnailUrl())
                        .accommodationId(req.getAccommodationId())
                    .build());
    }
}
