package huy.project.rating_service.infrastructure.repository.mapper;

import huy.project.rating_service.core.domain.entity.MonthlyRatingEntity;
import huy.project.rating_service.infrastructure.repository.model.MonthlyRatingModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public abstract class MonthlyRatingMapper {
    public static final MonthlyRatingMapper INSTANCE = Mappers.getMapper(MonthlyRatingMapper.class);

    public abstract MonthlyRatingEntity toEntity(MonthlyRatingModel model);

    public abstract MonthlyRatingModel toModel(MonthlyRatingEntity entity);

    public abstract List<MonthlyRatingEntity> toListEntity(List<MonthlyRatingModel> models);
}
