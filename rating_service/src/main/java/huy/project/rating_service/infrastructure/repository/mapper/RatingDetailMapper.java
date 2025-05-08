package huy.project.rating_service.infrastructure.repository.mapper;

import huy.project.rating_service.core.domain.entity.RatingDetailEntity;
import huy.project.rating_service.infrastructure.repository.model.RatingDetailModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public abstract class RatingDetailMapper {
    public static final RatingDetailMapper INSTANCE = Mappers.getMapper( RatingDetailMapper.class );

    public abstract RatingDetailModel toModel(RatingDetailEntity entity);

    public abstract RatingDetailEntity toEntity(RatingDetailModel model);

    public abstract List<RatingDetailEntity> toListEntity(List<RatingDetailModel> models);

    public abstract List<RatingDetailModel> toListModel(List<RatingDetailEntity> entities);
}
