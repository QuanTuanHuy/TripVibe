package huy.project.profile_service.core.service;

import huy.project.profile_service.core.domain.dto.request.CreateWishlistDto;
import huy.project.profile_service.core.domain.dto.request.CreateWishlistItemDto;
import huy.project.profile_service.core.domain.entity.WishlistEntity;

public interface IWishlistService {
    WishlistEntity createWishlist(Long touristId, CreateWishlistDto req);
    void addWishlistItem(Long touristId, Long wishlistId, CreateWishlistItemDto req);
}
