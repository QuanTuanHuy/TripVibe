package huy.project.profile_service.core.service;

import huy.project.profile_service.core.domain.dto.request.CreateWishlistDto;
import huy.project.profile_service.core.domain.dto.request.CreateWishlistItemDto;
import huy.project.profile_service.core.domain.entity.WishlistEntity;

import java.util.List;

public interface IWishlistService {
    WishlistEntity createWishlist(Long touristId, CreateWishlistDto req);
    void addWishlistItem(Long touristId, Long wishlistId, CreateWishlistItemDto req);
    WishlistEntity getWishlistById(Long touristId, Long wishlistId);
    List<WishlistEntity> getWishlistByTouristId(Long touristId);
    void deleteWishlistItem(Long touristId, Long wishlistId, Long itemId);
    void deleteWishlist(Long touristId, Long wishlistId);
}
