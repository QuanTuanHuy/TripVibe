package huy.project.profile_service.core.domain.mapper;

import huy.project.profile_service.core.domain.dto.request.CreateFriendDto;
import huy.project.profile_service.core.domain.entity.FriendEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class FriendMapper {
    public static final FriendMapper INSTANCE = Mappers.getMapper(FriendMapper.class);

    public abstract FriendEntity toEntity(CreateFriendDto dto);
}
