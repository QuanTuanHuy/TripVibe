package huy.project.accommodation_service.core.domain.entity;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AccommodationEntity {
    private Long id;
    private Long hostId;
    private Long locationId;
    private Long typeId;
    private Long currencyId;
    private String name;
    private String description;
    private Long checkInTimeFrom;
    private Long checkInTimeTo;
    private Long checkOutTimeFrom;
    private Long checkOutTimeTo;
    private Boolean isVerified;

    private List<AccommodationAmenityEntity> amenities;
    private List<UnitEntity> units;
    private List<LanguageEntity> languages;
    private LocationEntity location;
    private AccommodationTypeEntity type;
}
