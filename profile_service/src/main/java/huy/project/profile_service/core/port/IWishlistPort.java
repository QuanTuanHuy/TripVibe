package huy.project.profile_service.core.port;

import huy.project.profile_service.core.domain.entity.WishlistEntity;

import java.util.List;

public interface IWishlistPort {
    WishlistEntity save(WishlistEntity wishlist);
    List<WishlistEntity> getWishlistByTouristId(Long touristId);
    WishlistEntity getWishlistById(Long id);
}
