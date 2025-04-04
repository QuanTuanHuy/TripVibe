package huy.project.profile_service.core.domain.mapper;

import huy.project.profile_service.core.domain.dto.request.UserProfileDto;
import huy.project.profile_service.core.domain.entity.TouristEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class UserProfileMapper {
    public static final UserProfileMapper INSTANCE = Mappers.getMapper(UserProfileMapper.class);

    public abstract UserProfileDto toUserProfileDto(TouristEntity tourist);
}
