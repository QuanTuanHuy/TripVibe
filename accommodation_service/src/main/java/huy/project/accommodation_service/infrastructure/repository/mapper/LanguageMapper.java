package huy.project.accommodation_service.infrastructure.repository.mapper;

import huy.project.accommodation_service.core.domain.entity.LanguageEntity;
import huy.project.accommodation_service.infrastructure.repository.model.LanguageModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class LanguageMapper {
    public static final LanguageMapper INSTANCE = Mappers.getMapper(LanguageMapper.class);

    public abstract LanguageEntity toEntity(LanguageModel language);

    public abstract LanguageModel toModel(LanguageEntity language);
}
