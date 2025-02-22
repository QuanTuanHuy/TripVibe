package huy.project.authentication_service.infrastructure.repository.mapper;

import huy.project.authentication_service.core.domain.entity.RolePrivilegeEntity;
import huy.project.authentication_service.infrastructure.repository.model.RolePrivilegeModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public abstract class RolePrivilegeMapper {
    public static final RolePrivilegeMapper INSTANCE = Mappers.getMapper(RolePrivilegeMapper.class);

    public abstract RolePrivilegeEntity toEntity(RolePrivilegeModel rolePrivilege);
    public abstract RolePrivilegeModel toModel(RolePrivilegeEntity rolePrivilege);
    public abstract List<RolePrivilegeEntity> toListEntity(List<RolePrivilegeModel> rolePrivileges);
    public abstract List<RolePrivilegeModel> toListModel(List<RolePrivilegeEntity> rolePrivileges);
}
