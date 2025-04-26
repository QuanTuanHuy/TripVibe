package huy.project.profile_service.core.port;

import huy.project.profile_service.core.domain.entity.WishlistItemEntity;

import java.util.List;

public interface IWishlistItemPort {
    WishlistItemEntity save(WishlistItemEntity wishlistItem);
    List<WishlistItemEntity> saveAll(List<WishlistItemEntity> wishlistItems);
    WishlistItemEntity getWishListByAccIdAndWishlistId(Long accId, Long wishlistId);
    List<WishlistItemEntity> getWishlistByAccIdsAndWishlistId(List<Long> accIds, Long wishlistId);
    List<WishlistItemEntity> getWishlistsByWishlistId(Long wishlistId);
}
