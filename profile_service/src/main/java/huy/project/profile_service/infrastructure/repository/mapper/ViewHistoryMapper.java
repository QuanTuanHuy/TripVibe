package huy.project.profile_service.infrastructure.repository.mapper;

import huy.project.profile_service.core.domain.entity.ViewHistoryEntity;
import huy.project.profile_service.infrastructure.repository.model.ViewHistoryModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public abstract class ViewHistoryMapper {
    public static final ViewHistoryMapper INSTANCE = Mappers.getMapper(ViewHistoryMapper.class);

    public abstract ViewHistoryEntity toEntity(ViewHistoryModel model);
    public abstract ViewHistoryModel toModel(ViewHistoryEntity entity);
    public abstract List<ViewHistoryEntity> toListEntity(List<ViewHistoryModel> models);
}
