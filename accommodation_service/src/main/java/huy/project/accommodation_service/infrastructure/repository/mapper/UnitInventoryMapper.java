package huy.project.accommodation_service.infrastructure.repository.mapper;

import huy.project.accommodation_service.core.domain.entity.UnitInventoryEntity;
import huy.project.accommodation_service.infrastructure.repository.model.UnitInventoryModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.List;

@Mapper
public abstract class UnitInventoryMapper {
    public static final UnitInventoryMapper INSTANCE = Mappers.getMapper(UnitInventoryMapper.class);

    @Mapping(target = "lastSyncTimestamp", ignore = true)
    public abstract UnitInventoryEntity toEntity(UnitInventoryModel model);

    @Mapping(target = "lastSyncTimestamp", ignore = true)
    public abstract UnitInventoryModel toModel(UnitInventoryEntity entity);

    public abstract List<UnitInventoryEntity> toListEntity(List<UnitInventoryModel> models);

    public abstract List<UnitInventoryModel> toListModel(List<UnitInventoryEntity> entities);

    @Named("fromLongToInstant")
    public Long fromInstantToLong(Instant instant) {
        return instant.toEpochMilli();
    }

    @Named("fromInstantToLong")
    public Instant fromLongToInstant(Long timestamp) {
        return Instant.ofEpochMilli(timestamp);
    }
}
