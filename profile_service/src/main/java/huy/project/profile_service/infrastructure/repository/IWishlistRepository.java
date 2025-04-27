package huy.project.profile_service.infrastructure.repository;

import huy.project.profile_service.infrastructure.repository.model.WishlistModel;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IWishlistRepository extends IBaseRepository<WishlistModel> {
    List<WishlistModel> findByTouristId(Long touristId);
}
