package huy.project.rating_service.infrastructure.repository.mapper;

import huy.project.rating_service.core.domain.entity.PendingReviewEntity;
import huy.project.rating_service.infrastructure.repository.model.PendingReviewModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public abstract class PendingReviewMapper {
    public static final PendingReviewMapper INSTANCE = Mappers.getMapper(PendingReviewMapper.class);

    public abstract PendingReviewEntity toEntity(PendingReviewModel model);

    public abstract PendingReviewModel toModel(PendingReviewEntity entity);

    public abstract List<PendingReviewEntity> toListEntity(List<PendingReviewModel> models);
}
