package huy.project.authentication_service.infrastructure.repository.mapper;

import huy.project.authentication_service.core.domain.entity.RoleEntity;
import huy.project.authentication_service.infrastructure.repository.model.RoleModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public abstract class RoleMapper {
    public static final RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    public abstract RoleEntity toEntity(RoleModel role);
    public abstract RoleModel toModel(RoleEntity role);
    public abstract List<RoleEntity> toListRoleEntity(List<RoleModel> roles);
}
