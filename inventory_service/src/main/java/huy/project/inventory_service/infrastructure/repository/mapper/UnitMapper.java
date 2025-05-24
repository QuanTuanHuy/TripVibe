package huy.project.inventory_service.infrastructure.repository.mapper;

import huy.project.inventory_service.core.domain.entity.Unit;
import huy.project.inventory_service.infrastructure.repository.model.UnitModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public abstract class UnitMapper {
    
    public static final UnitMapper INSTANCE = Mappers.getMapper(UnitMapper.class);
    
    public abstract Unit toEntity(UnitModel model);

    public abstract UnitModel toModel(Unit entity);
    
    public abstract List<Unit> toEntityList(List<UnitModel> models);
    
    public abstract List<UnitModel> toModelList(List<Unit> entities);
}
