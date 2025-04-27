package huy.project.profile_service.infrastructure.repository;

import huy.project.profile_service.infrastructure.repository.model.WishlistItemModel;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IWishlistItemRepository extends IBaseRepository<WishlistItemModel> {
    Optional<WishlistItemModel> findByWishlistIdAndAccommodationId(Long wishlistId, Long accommodationId);

    List<WishlistItemModel> findByWishlistIdAndAccommodationIdIn(Long wishlistId, List<Long> accommodationIds);

    List<WishlistItemModel> findByWishlistId(Long wishlistId);
}
