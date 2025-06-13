package huy.project.profile_service.core.usecase;

import huy.project.profile_service.core.domain.constant.ErrorCode;
import huy.project.profile_service.core.domain.entity.WishlistEntity;
import huy.project.profile_service.core.domain.exception.AppException;
import huy.project.profile_service.core.port.IWishlistItemPort;
import huy.project.profile_service.core.port.IWishlistPort;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class GetWishlistUseCase {
    IWishlistPort wishlistPort;
    IWishlistItemPort wishlistItemPort;

    public WishlistEntity getDetailWishlist(Long touristId, Long wishlistId) {
        WishlistEntity wishlist = wishlistPort.getWishlistById(wishlistId);
        if (wishlist == null) {
            log.error("wishlist not found with id: {}", wishlistId);
            throw new AppException(ErrorCode.WISHLIST_NOT_FOUND);
        }

        if (!wishlist.getTouristId().equals(touristId)) {
            log.error("tourist {} is not allowed to get wishlist {}", touristId, wishlistId);
            throw new AppException(ErrorCode.FORBIDDEN_GET_WISHLIST);
        }

        wishlist.setItems(wishlistItemPort.getWishlistsByWishlistId(wishlistId));

        return wishlist;
    }

    public List<WishlistEntity> getWishlistByTouristId(Long touristId) {
        List<WishlistEntity> wishLists = wishlistPort.getWishlistByTouristId(touristId);
        if (CollectionUtils.isEmpty(wishLists)) {
            return Collections.emptyList();
        }

        List<Long> wishlistIds = wishLists.stream().map(WishlistEntity::getId).toList();
        var wishlistItems = wishlistItemPort.getItemsByWishlistIds(wishlistIds);

        wishLists.forEach(wishlist -> wishlist.setItems(wishlistItems.stream()
                .filter(item -> item.getWishlistId().equals(wishlist.getId()))
                .toList()));

        return wishLists;
    }
}
