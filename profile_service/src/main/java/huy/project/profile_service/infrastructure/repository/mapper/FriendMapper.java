package huy.project.profile_service.infrastructure.repository.mapper;

import huy.project.profile_service.core.domain.entity.FriendEntity;
import huy.project.profile_service.infrastructure.repository.model.FriendModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public abstract class FriendMapper {
    public static final FriendMapper INSTANCE = Mappers.getMapper(FriendMapper.class);

    public abstract FriendModel toModel(FriendEntity friend);
    public abstract FriendEntity toEntity(FriendModel friend);
    public abstract List<FriendEntity> toListEntity(List<FriendModel> friends);
}
