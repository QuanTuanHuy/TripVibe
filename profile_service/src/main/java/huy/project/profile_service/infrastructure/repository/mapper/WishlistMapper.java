package huy.project.profile_service.infrastructure.repository.mapper;

import huy.project.profile_service.core.domain.entity.WishlistEntity;
import huy.project.profile_service.infrastructure.repository.model.WishlistModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public abstract class WishlistMapper {
    public static final WishlistMapper INSTANCE = Mappers.getMapper(WishlistMapper.class);

    public abstract WishlistEntity toEntity(WishlistModel model);

    public abstract WishlistModel toModel(WishlistEntity entity);

    public abstract List<WishlistEntity> toListEntity(List<WishlistModel> models);
}
