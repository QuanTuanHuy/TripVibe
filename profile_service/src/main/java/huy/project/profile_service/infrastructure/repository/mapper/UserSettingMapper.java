package huy.project.profile_service.infrastructure.repository.mapper;

import huy.project.profile_service.core.domain.entity.UserSettingEntity;
import huy.project.profile_service.infrastructure.repository.model.UserSettingModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class UserSettingMapper {
    public static final UserSettingMapper INSTANCE = Mappers.getMapper(UserSettingMapper.class);

    public abstract UserSettingEntity toEntity(UserSettingModel model);
    public abstract UserSettingModel toModel(UserSettingEntity entity);
}
