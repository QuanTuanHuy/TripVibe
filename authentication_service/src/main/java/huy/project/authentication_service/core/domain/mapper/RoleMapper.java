package huy.project.authentication_service.core.domain.mapper;

import huy.project.authentication_service.core.domain.dto.request.CreateRoleRequestDto;
import huy.project.authentication_service.core.domain.dto.request.UpdateRoleRequestDto;
import huy.project.authentication_service.core.domain.entity.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class RoleMapper {
    public static final RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    public abstract RoleEntity toEntity(CreateRoleRequestDto req);

    public RoleEntity toEntity(RoleEntity role, UpdateRoleRequestDto req) {
        role.setName(req.getName());
        role.setDescription(req.getDescription());
        return role;
    }
}
