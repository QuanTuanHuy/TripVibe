package huy.project.search_service.infrastructure.repository.mapper;

import huy.project.search_service.core.domain.entity.AccommodationEntity;
import huy.project.search_service.infrastructure.repository.document.AccommodationDocument;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public abstract class AccommodationMapper {
    public static final AccommodationMapper INSTANCE = Mappers.getMapper(AccommodationMapper.class);

    public abstract AccommodationEntity toEntity(AccommodationDocument accommodation);
    public abstract AccommodationDocument toDocument(AccommodationEntity accommodation);
    public abstract List<AccommodationEntity> toListEntity(List<AccommodationDocument> accommodations);
}
