package huy.project.rating_service.infrastructure.repository.mapper;

import huy.project.rating_service.core.domain.entity.RatingSummaryEntity;
import huy.project.rating_service.infrastructure.repository.model.RatingSummaryModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public abstract class RatingSummaryMapper {
    public static final RatingSummaryMapper INSTANCE = Mappers.getMapper(RatingSummaryMapper.class);

    public abstract RatingSummaryEntity toEntity(RatingSummaryModel model);
    public abstract RatingSummaryModel toModel(RatingSummaryEntity entity);
    public abstract List<RatingSummaryEntity> toListEntity(List<RatingSummaryModel> models);
}
