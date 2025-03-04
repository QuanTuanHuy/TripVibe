package huy.project.accommodation_service.infrastructure.repository.mapper;

import huy.project.accommodation_service.core.domain.entity.BedroomEntity;
import huy.project.accommodation_service.infrastructure.repository.model.BedroomModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public abstract class BedroomMapper {
    public static final BedroomMapper INSTANCE = Mappers.getMapper(BedroomMapper.class);

    public abstract BedroomEntity toEntity(BedroomModel model);
    public abstract BedroomModel toModel(BedroomEntity entity);
    public abstract List<BedroomEntity> toListEntity(List<BedroomModel> models);
}
