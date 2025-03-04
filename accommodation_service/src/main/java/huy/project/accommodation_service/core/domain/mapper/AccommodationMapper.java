package huy.project.accommodation_service.core.domain.mapper;

import huy.project.accommodation_service.core.domain.dto.request.CreateAccommodationDto;
import huy.project.accommodation_service.core.domain.entity.AccommodationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AccommodationMapper {
    public static final AccommodationMapper INSTANCE = Mappers.getMapper(AccommodationMapper.class);

    public AccommodationEntity toEntity(Long userId, Long locationId, CreateAccommodationDto req) {
        return AccommodationEntity.builder()
                .hostId(userId)
                .locationId(locationId)
                .name(req.getName())
                .description(req.getDescription())
                .typeId(req.getTypeId())
                .currencyId(req.getCurrencyId())
                .checkInTimeFrom(req.getCheckInTimeFrom())
                .checkInTimeTo(req.getCheckInTimeTo())
                .checkOutTimeFrom(req.getCheckOutTimeFrom())
                .checkOutTimeTo(req.getCheckOutTimeTo())
                .isVerified(false)
                .build();
    }
}
