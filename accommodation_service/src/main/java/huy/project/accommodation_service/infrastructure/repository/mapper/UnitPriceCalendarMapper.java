package huy.project.accommodation_service.infrastructure.repository.mapper;

import huy.project.accommodation_service.core.domain.entity.UnitPriceCalendarEntity;
import huy.project.accommodation_service.infrastructure.repository.model.UnitPriceCalendarModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public abstract class UnitPriceCalendarMapper {
    public static final UnitPriceCalendarMapper INSTANCE = Mappers.getMapper(UnitPriceCalendarMapper.class);

    public abstract UnitPriceCalendarModel toModel(UnitPriceCalendarEntity entity);
    public abstract UnitPriceCalendarEntity toEntity(UnitPriceCalendarModel model);
    public abstract List<UnitPriceCalendarModel> toListModel(List<UnitPriceCalendarEntity> entities);
    public abstract List<UnitPriceCalendarEntity> toListEntity(List<UnitPriceCalendarModel> models);
}
