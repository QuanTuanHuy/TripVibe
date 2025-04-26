package huy.project.profile_service.core.service.impl;

import huy.project.profile_service.core.domain.dto.request.CreateWishlistDto;
import huy.project.profile_service.core.domain.dto.request.CreateWishlistItemDto;
import huy.project.profile_service.core.domain.entity.WishlistEntity;
import huy.project.profile_service.core.service.IWishlistService;
import huy.project.profile_service.core.usecase.CreateWishlistUseCase;
import huy.project.profile_service.core.usecase.UpdateWishlistUseCase;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WishlistService implements IWishlistService {
    CreateWishlistUseCase createWishlistUseCase;
    UpdateWishlistUseCase updateWishlistUseCase;

    @Override
    public WishlistEntity createWishlist(Long touristId, CreateWishlistDto req) {
        return createWishlistUseCase.createWishlist(touristId, req);
    }

    @Override
    public void addWishlistItem(Long touristId, Long wishlistId, CreateWishlistItemDto req) {
        updateWishlistUseCase.addWishlistItem(touristId, wishlistId, req);
    }
}
