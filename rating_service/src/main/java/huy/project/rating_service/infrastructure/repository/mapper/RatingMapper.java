package huy.project.rating_service.infrastructure.repository.mapper;

import huy.project.rating_service.core.domain.entity.RatingEntity;
import huy.project.rating_service.infrastructure.repository.model.RatingModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.List;

@Mapper
public abstract class RatingMapper {
    public static final RatingMapper INSTANCE = Mappers.getMapper(RatingMapper.class);

    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "fromLongToInstant")
    public abstract RatingEntity toEntity(RatingModel rating);
    public abstract RatingModel toModel(RatingEntity rating);

    @Named("fromLongToInstant")
    public Long fromInstantToLong(Instant instant) {
        return instant.toEpochMilli();
    }

    public abstract List<RatingEntity> toListEntity(List<RatingModel> ratings);
}
