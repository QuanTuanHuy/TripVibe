package huy.project.rating_service.infrastructure.repository.mapper;

import huy.project.rating_service.core.domain.entity.RatingResponseEntity;
import huy.project.rating_service.infrastructure.repository.model.RatingResponseModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.List;

@Mapper
public abstract class RatingResponseMapper {
    public static final RatingResponseMapper INSTANCE = Mappers.getMapper(RatingResponseMapper.class);

    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "fromLongToInstant")
    public abstract RatingResponseEntity toEntity(RatingResponseModel model);

    public abstract RatingResponseModel toModel(RatingResponseEntity entity);

    public abstract List<RatingResponseEntity> toListEntity(List<RatingResponseModel> models);

    @Named("fromLongToInstant")
    public Long fromInstantToLong(Instant instant) {
        return instant.toEpochMilli();
    }
}
