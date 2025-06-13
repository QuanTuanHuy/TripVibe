package huy.project.profile_service.infrastructure.repository.adapter;

import huy.project.profile_service.core.domain.constant.ErrorCode;
import huy.project.profile_service.core.domain.entity.WishlistEntity;
import huy.project.profile_service.core.domain.exception.AppException;
import huy.project.profile_service.core.port.IWishlistPort;
import huy.project.profile_service.infrastructure.repository.IWishlistRepository;
import huy.project.profile_service.infrastructure.repository.mapper.WishlistMapper;
import huy.project.profile_service.infrastructure.repository.model.WishlistModel;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class WishlistAdapter implements IWishlistPort {
    IWishlistRepository wishlistRepository;

    @Override
    public WishlistEntity save(WishlistEntity wishlist) {
        try {
            WishlistModel model = WishlistMapper.INSTANCE.toModel(wishlist);
            return WishlistMapper.INSTANCE.toEntity(wishlistRepository.save(model));
        } catch (Exception e) {
            log.error("Error while saving wishlist: {}", e.getMessage());
            throw new AppException(ErrorCode.SAVE_WISHLIST_FAILED);
        }
    }

    @Override
    public List<WishlistEntity> getWishlistByTouristId(Long touristId) {
        return WishlistMapper.INSTANCE.toListEntity(wishlistRepository.findByTouristId(touristId));
    }

    @Override
    public WishlistEntity getWishlistById(Long id) {
        return WishlistMapper.INSTANCE.toEntity(wishlistRepository.findById(id).orElse(null));
    }

    @Override
    public void deleteWishlistById(Long id) {
        wishlistRepository.deleteById(id);
    }
}
