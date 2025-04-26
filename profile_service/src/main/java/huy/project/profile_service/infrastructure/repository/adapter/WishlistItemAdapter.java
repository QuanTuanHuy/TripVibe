package huy.project.profile_service.infrastructure.repository.adapter;

import huy.project.profile_service.core.domain.entity.WishlistItemEntity;
import huy.project.profile_service.core.port.IWishlistItemPort;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WishlistItemAdapter implements IWishlistItemPort {
    @Override
    public WishlistItemEntity save(WishlistItemEntity wishlistItem) {
        return null;
    }

    @Override
    public List<WishlistItemEntity> saveAll(List<WishlistItemEntity> wishlistItems) {
        return List.of();
    }

    @Override
    public WishlistItemEntity getWishListByAccIdAndWishlistId(Long accId, Long wishlistId) {
        return null;
    }

    @Override
    public List<WishlistItemEntity> getWishlistByAccIdsAndWishlistId(List<Long> accIds, Long wishlistId) {
        return List.of();
    }

    @Override
    public List<WishlistItemEntity> getWishlistsByWishlistId(Long wishlistId) {
        return List.of();
    }
}
