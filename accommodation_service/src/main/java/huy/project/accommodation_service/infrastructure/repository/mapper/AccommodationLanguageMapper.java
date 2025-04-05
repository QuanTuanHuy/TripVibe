package huy.project.accommodation_service.infrastructure.repository.mapper;

import huy.project.accommodation_service.core.domain.entity.AccommodationLanguageEntity;
import huy.project.accommodation_service.infrastructure.repository.model.AccommodationLanguageModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public abstract class AccommodationLanguageMapper {
    public static final AccommodationLanguageMapper INSTANCE = Mappers.getMapper(AccommodationLanguageMapper.class);

    public abstract AccommodationLanguageEntity toEntity(AccommodationLanguageModel model);
    public abstract AccommodationLanguageModel toModel(AccommodationLanguageEntity entity);
    public abstract List<AccommodationLanguageModel> toListModel(List<AccommodationLanguageEntity> entities);
    public abstract List<AccommodationLanguageEntity> toListEntity(List<AccommodationLanguageModel> models);
}
