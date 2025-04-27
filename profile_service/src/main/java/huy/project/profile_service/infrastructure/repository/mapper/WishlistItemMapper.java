package huy.project.profile_service.infrastructure.repository.mapper;

import huy.project.profile_service.core.domain.entity.WishlistItemEntity;
import huy.project.profile_service.infrastructure.repository.model.WishlistItemModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public abstract class WishlistItemMapper {
    public static final WishlistItemMapper INSTANCE = Mappers.getMapper(WishlistItemMapper.class);

    public abstract WishlistItemEntity toEntity(WishlistItemModel model);

    public abstract WishlistItemModel toModel(WishlistItemEntity entity);

    public abstract List<WishlistItemEntity> toListEntity(List<WishlistItemModel> models);

    public abstract List<WishlistItemModel> toListModel(List<WishlistItemEntity> entities);
}
