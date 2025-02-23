package huy.project.authentication_service.infrastructure.repository.mapper;

import huy.project.authentication_service.core.domain.entity.RoleEntity;
import huy.project.authentication_service.core.domain.entity.UserRoleEntity;
import huy.project.authentication_service.infrastructure.repository.model.RoleModel;
import huy.project.authentication_service.infrastructure.repository.model.UserRoleModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public abstract class UserRoleMapper {
    public static final UserRoleMapper INSTANCE = Mappers.getMapper(UserRoleMapper.class);

    public abstract UserRoleModel toEntity(RoleEntity userRole);

    public abstract UserRoleEntity toModel(RoleModel userRole);

    public abstract List<UserRoleEntity> toListEntity(List<UserRoleModel> userRoles);

    public abstract List<UserRoleModel> toListModel(List<UserRoleEntity> userRoles);
}
