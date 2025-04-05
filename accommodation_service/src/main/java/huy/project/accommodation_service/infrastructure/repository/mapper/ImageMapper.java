package huy.project.accommodation_service.infrastructure.repository.mapper;

import huy.project.accommodation_service.core.domain.entity.ImageEntity;
import huy.project.accommodation_service.infrastructure.repository.model.ImageModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public abstract class ImageMapper {
    public static final ImageMapper INSTANCE = Mappers.getMapper(ImageMapper.class);

    public abstract ImageEntity toEntity(ImageModel image);

    public abstract ImageModel toModel(ImageEntity image);

    public abstract List<ImageModel> toListModel(List<ImageEntity> images);

    public abstract List<ImageEntity> toListEntity(List<ImageModel> images);
}
