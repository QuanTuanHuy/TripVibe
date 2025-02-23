package huy.project.authentication_service.core.domain.mapper;

import huy.project.authentication_service.core.domain.dto.request.CreateUserRequestDto;
import huy.project.authentication_service.core.domain.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class UserMapper {
    public static final UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    public abstract UserEntity toEntity(CreateUserRequestDto request);
}
