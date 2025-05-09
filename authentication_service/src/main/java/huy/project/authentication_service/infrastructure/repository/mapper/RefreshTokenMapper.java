package huy.project.authentication_service.infrastructure.repository.mapper;

import huy.project.authentication_service.core.domain.entity.RefreshTokenEntity;
import huy.project.authentication_service.infrastructure.repository.model.RefreshTokenModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public abstract class RefreshTokenMapper {
    public static final RefreshTokenMapper INSTANCE = Mappers.getMapper(RefreshTokenMapper.class);

    public abstract RefreshTokenEntity toEntity(RefreshTokenModel model);

    public abstract RefreshTokenModel toModel(RefreshTokenEntity entity);

    public abstract List<RefreshTokenEntity> toListEntity(List<RefreshTokenModel> models);
}
