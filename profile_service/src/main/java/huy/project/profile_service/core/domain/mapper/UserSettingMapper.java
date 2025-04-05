package huy.project.profile_service.core.domain.mapper;

import huy.project.profile_service.core.domain.dto.request.UpdateUserSettingDto;
import huy.project.profile_service.core.domain.entity.UserSettingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class UserSettingMapper {
    public static final UserSettingMapper INSTANCE = Mappers.getMapper(UserSettingMapper.class);

    public abstract UserSettingEntity toEntity(UpdateUserSettingDto req);
}
