package huy.project.profile_service.core.domain.mapper;

import huy.project.profile_service.core.domain.dto.kafka.CreateViewHistoryMessage;
import huy.project.profile_service.core.domain.entity.ViewHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class ViewHistoryMapper {
    public static final ViewHistoryMapper INSTANCE = Mappers.getMapper(ViewHistoryMapper.class);

    public abstract ViewHistoryEntity toEntity(CreateViewHistoryMessage message);
}
