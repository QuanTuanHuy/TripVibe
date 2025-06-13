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

import java.util.List;

@RestController
@RequestMapping("/api/public/v1/wishlists")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WishlistController {
    IWishlistService wishlistService;

    @GetMapping
    public ResponseEntity<Resource<List<WishlistEntity>>> getWishlists() {
        Long touristId = AuthenUtils.getCurrentUserId();
        return ResponseEntity.ok(new Resource<>(wishlistService.getWishlistByTouristId(touristId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource<WishlistEntity>> getWishlistById(@PathVariable Long id) {
        Long touristId = AuthenUtils.getCurrentUserId();
        WishlistEntity wishlist = wishlistService.getWishlistById(touristId, id);
        return ResponseEntity.ok(new Resource<>(wishlist));
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Resource<?>> deleteWishlist(@PathVariable Long id) {
        Long touristId = AuthenUtils.getCurrentUserId();
        wishlistService.deleteWishlist(touristId, id);
        return ResponseEntity.ok(new Resource<>(null));
    }

    @DeleteMapping("/{id}/items/{itemId}")
    public ResponseEntity<Resource<?>> deleteWishlistItem(
            @PathVariable Long id,
            @PathVariable Long itemId
    ) {
        Long touristId = AuthenUtils.getCurrentUserId();
        wishlistService.deleteWishlistItem(touristId, id, itemId);
        return ResponseEntity.ok(new Resource<>(null));
    }
}
