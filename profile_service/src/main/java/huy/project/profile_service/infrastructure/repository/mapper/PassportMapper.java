package huy.project.profile_service.infrastructure.repository.mapper;

import huy.project.profile_service.core.domain.entity.PassportEntity;
import huy.project.profile_service.infrastructure.repository.model.PassportModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class PassportMapper {
    public static final PassportMapper INSTANCE = Mappers.getMapper(PassportMapper.class);

    public abstract PassportEntity toEntity(PassportModel model);
    public abstract PassportModel toModel(PassportEntity entity);
}
