package huy.project.profile_service.infrastructure.repository.adapter;

import huy.project.profile_service.core.domain.entity.WishlistEntity;
import huy.project.profile_service.core.port.IWishlistPort;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WishlistAdapter implements IWishlistPort {
    @Override
    public WishlistEntity save(WishlistEntity wishlist) {
        return null;
    }

    @Override
    public List<WishlistEntity> getWishlistByTouristId(Long touristId) {
        return null;
    }

    @Override
    public WishlistEntity getWishlistById(Long id) {
        return null;
    }
}
