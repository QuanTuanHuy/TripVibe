package huy.project.accommodation_service.core.domain.mapper;

import huy.project.accommodation_service.core.domain.dto.request.CreateLanguageRequestDto;
import huy.project.accommodation_service.core.domain.entity.LanguageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class LanguageMapper {
    public static final LanguageMapper INSTANCE = Mappers.getMapper(LanguageMapper.class);

    public abstract LanguageEntity toEntity(CreateLanguageRequestDto req);
}
