package huy.project.accommodation_service.core.domain.entity;

import lombok.*;

import java.time.LocalTime;
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
    private LocalTime checkInTimeFrom;
    private LocalTime checkInTimeTo;
    private LocalTime checkOutTimeFrom;
    private LocalTime checkOutTimeTo;
    private Boolean isVerified;

    private List<AccommodationAmenityEntity> amenities;
    private List<UnitEntity> units;
    private List<LanguageEntity> languages;
    private LocationEntity location;
    private AccommodationTypeEntity type;
}
