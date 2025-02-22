package huy.project.authentication_service.core.domain.mapper;

import huy.project.authentication_service.core.domain.dto.request.CreatePrivilegeRequestDto;
import huy.project.authentication_service.core.domain.dto.request.UpdatePrivilegeRequestDto;
import huy.project.authentication_service.core.domain.entity.PrivilegeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class PrivilegeMapper {
    public static final PrivilegeMapper INSTANCE = Mappers.getMapper(PrivilegeMapper.class);

    public abstract PrivilegeEntity toEntity(CreatePrivilegeRequestDto req);

    public PrivilegeEntity toEntity(PrivilegeEntity entity, UpdatePrivilegeRequestDto req) {
        entity.setName(req.getName());
        entity.setDescription(req.getDescription());
        return entity;
    }
}
