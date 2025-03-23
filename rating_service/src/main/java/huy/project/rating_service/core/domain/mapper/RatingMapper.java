package huy.project.rating_service.core.domain.mapper;

import huy.project.rating_service.core.domain.dto.CreateRatingDto;
import huy.project.rating_service.core.domain.entity.RatingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class RatingMapper {
    public static final RatingMapper INSTANCE = Mappers.getMapper(RatingMapper.class);

    public abstract RatingEntity toEntity(CreateRatingDto req);
}
