package huy.project.profile_service.ui.controller;

import huy.project.profile_service.core.domain.dto.request.CreateWishlistDto;
import huy.project.profile_service.core.domain.dto.request.CreateWishlistItemDto;
import huy.project.profile_service.core.domain.entity.WishlistEntity;
import huy.project.profile_service.core.service.IWishlistService;
import huy.project.profile_service.kernel.utils.AuthenUtils;
import huy.project.profile_service.ui.resource.Resource;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/v1/wishlists")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WishlistController {
    IWishlistService wishlistService;

    @PostMapping
    public ResponseEntity<Resource<WishlistEntity>> createWishlist(@RequestBody CreateWishlistDto request) {
        Long touristId = AuthenUtils.getCurrentUserId();
        WishlistEntity wishlist = wishlistService.createWishlist(touristId, request);
        return ResponseEntity.ok(new Resource<>(wishlist));
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<Resource<?>> addWishlistItem(
            @PathVariable Long id,
            @RequestBody CreateWishlistItemDto request
    ) {
        Long touristId = AuthenUtils.getCurrentUserId();
        wishlistService.addWishlistItem(touristId, id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new Resource<>(null));
    }
}
