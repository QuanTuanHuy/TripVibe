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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class DeleteWishlistUseCase {
    IWishlistPort wishlistPort;
    IWishlistItemPort wishlistItemPort;

    @Transactional(rollbackFor = Exception.class)
    public void deleteWishlist(Long touristId, Long wishlistId) {
        checkWishlistExists(touristId, wishlistId);

        wishlistItemPort.deleteByWishlistId(wishlistId);
        wishlistPort.deleteWishlistById(wishlistId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteWishlistItem(Long touristId, Long wishlistId, Long itemId) {
        checkWishlistExists(touristId, wishlistId);

        var item = wishlistItemPort.getWishlistItemById(itemId);
        if (item == null || !item.getWishlistId().equals(wishlistId)) {
            log.error("wishlist item not found with id: {}", itemId);
            throw new AppException(ErrorCode.WISHLIST_ITEM_NOT_FOUND);
        }

        wishlistItemPort.deleteById(itemId);
    }

    private void checkWishlistExists(Long touristId, Long wishlistId) {
        WishlistEntity wishlist = wishlistPort.getWishlistById(wishlistId);
        if (wishlist == null || !wishlist.getTouristId().equals(touristId)) {
            log.error("wishlist not found with id: {}", wishlistId);
            throw new AppException(ErrorCode.WISHLIST_NOT_FOUND);
        }
    }

}
