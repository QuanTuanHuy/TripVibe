package huy.project.profile_service.core.domain.mapper;

import huy.project.profile_service.core.domain.dto.request.UpdatePassportDto;
import huy.project.profile_service.core.domain.entity.PassportEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class PassportMapper {
    public static final PassportMapper INSTANCE = Mappers.getMapper(PassportMapper.class);

    public abstract PassportEntity toEntity(UpdatePassportDto req);
}
