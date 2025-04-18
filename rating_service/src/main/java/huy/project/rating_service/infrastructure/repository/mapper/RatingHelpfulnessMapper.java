package huy.project.rating_service.infrastructure.repository.mapper;

import huy.project.rating_service.core.domain.entity.RatingHelpfulnessEntity;
import huy.project.rating_service.infrastructure.repository.model.RatingHelpfulnessModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class RatingHelpfulnessMapper {
    public static final RatingHelpfulnessMapper INSTANCE = Mappers.getMapper(RatingHelpfulnessMapper.class);

    public abstract RatingHelpfulnessEntity toEntity(RatingHelpfulnessModel model);

    public abstract RatingHelpfulnessModel toModel(RatingHelpfulnessEntity entity);
}
