package huy.project.authentication_service.infrastructure.repository.mapper;

import huy.project.authentication_service.core.domain.dto.request.UpdateUserRequestDto;
import huy.project.authentication_service.core.domain.entity.UserEntity;
import huy.project.authentication_service.infrastructure.repository.model.UserModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class UserMapper {
    public static final UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    public abstract UserEntity toEntity(UserModel user);

    public abstract UserModel toModel(UserEntity user);

    public UserEntity toEntity(UserEntity existedUser, UpdateUserRequestDto req) {
        existedUser.setName(req.getName());
        existedUser.setUsername(req.getUsername());
        existedUser.setEmail(req.getEmail());
        return existedUser;
    }
}
