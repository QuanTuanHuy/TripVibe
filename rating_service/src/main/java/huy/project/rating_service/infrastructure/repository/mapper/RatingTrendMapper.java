package huy.project.rating_service.infrastructure.repository.mapper;

import huy.project.rating_service.core.domain.entity.RatingTrendEntity;
import huy.project.rating_service.infrastructure.repository.model.RatingTrendModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class RatingTrendMapper {
    public static final RatingTrendMapper INSTANCE = Mappers.getMapper(RatingTrendMapper.class);

    public abstract RatingTrendEntity toEntity(RatingTrendModel model);

    public abstract RatingTrendModel toModel(RatingTrendEntity entity);
}
