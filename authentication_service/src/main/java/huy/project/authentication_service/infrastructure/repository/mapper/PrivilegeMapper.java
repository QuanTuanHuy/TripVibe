package huy.project.authentication_service.infrastructure.repository.mapper;

import huy.project.authentication_service.core.domain.entity.PrivilegeEntity;
import huy.project.authentication_service.infrastructure.repository.model.PrivilegeModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public abstract class PrivilegeMapper {
    public static final PrivilegeMapper INSTANCE = Mappers.getMapper(PrivilegeMapper.class);

    public abstract PrivilegeEntity toEntity(PrivilegeModel privilege);

    public abstract PrivilegeModel toModel(PrivilegeEntity privilege);

    public abstract List<PrivilegeEntity> toListPrivilegeEntity(List<PrivilegeModel> privileges);
}
