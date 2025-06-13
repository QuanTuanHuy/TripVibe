package huy.project.profile_service.infrastructure.repository.adapter;

import huy.project.profile_service.core.domain.constant.ErrorCode;
import huy.project.profile_service.core.domain.entity.WishlistItemEntity;
import huy.project.profile_service.core.domain.exception.AppException;
import huy.project.profile_service.core.port.IWishlistItemPort;
import huy.project.profile_service.infrastructure.repository.IWishlistItemRepository;
import huy.project.profile_service.infrastructure.repository.mapper.WishlistItemMapper;
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
public class WishlistItemAdapter implements IWishlistItemPort {
    IWishlistItemRepository wishlistItemRepository;

    @Override
    public WishlistItemEntity save(WishlistItemEntity wishlistItem) {
        try {
            var itemModel = WishlistItemMapper.INSTANCE.toModel(wishlistItem);
            return WishlistItemMapper.INSTANCE.toEntity(wishlistItemRepository.save(itemModel));
        } catch (Exception e) {
            log.error("Error while saving wishlist item: {}", e.getMessage());
            throw new AppException(ErrorCode.SAVE_WISHLIST_ITEM_FAILED);
        }
    }

    @Override
    public List<WishlistItemEntity> saveAll(List<WishlistItemEntity> wishlistItems) {
        try {
            var itemModels = WishlistItemMapper.INSTANCE.toListModel(wishlistItems);
            return WishlistItemMapper.INSTANCE.toListEntity(wishlistItemRepository.saveAll(itemModels));
        } catch (Exception e) {
            log.error("Error while saving wishlist items: {}", e.getMessage());
            throw new AppException(ErrorCode.SAVE_WISHLIST_ITEM_FAILED);
        }
    }

    @Override
    public WishlistItemEntity getWishListByAccIdAndWishlistId(Long accId, Long wishlistId) {
        return WishlistItemMapper.INSTANCE.toEntity(
                wishlistItemRepository.findByWishlistIdAndAccommodationId(wishlistId, accId).orElse(null));
    }

    @Override
    public List<WishlistItemEntity> getWishlistByAccIdsAndWishlistId(List<Long> accIds, Long wishlistId) {
        return WishlistItemMapper.INSTANCE.toListEntity(
                wishlistItemRepository.findByWishlistIdAndAccommodationIdIn(wishlistId, accIds));
    }

    @Override
    public List<WishlistItemEntity> getWishlistsByWishlistId(Long wishlistId) {
        return WishlistItemMapper.INSTANCE.toListEntity(wishlistItemRepository.findByWishlistId(wishlistId));
    }

    @Override
    public List<WishlistItemEntity> getItemsByWishlistIds(List<Long> wishlistIds) {
        return WishlistItemMapper.INSTANCE.toListEntity(wishlistItemRepository.findByWishlistIdIn(wishlistIds));
    }

    @Override
    public void deleteByWishlistId(Long wishlistId) {
        wishlistItemRepository.deleteByWishlistId(wishlistId);
    }

    @Override
    public void deleteById(Long id) {
        wishlistItemRepository.deleteById(id);
    }

    @Override
    public WishlistItemEntity getWishlistItemById(Long id) {
        return WishlistItemMapper.INSTANCE.toEntity(wishlistItemRepository.findById(id).orElse(null));
    }
}
