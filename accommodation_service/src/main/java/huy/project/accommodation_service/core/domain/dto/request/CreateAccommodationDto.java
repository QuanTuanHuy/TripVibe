package huy.project.accommodation_service.core.domain.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreateAccommodationDto {
    private Long typeId;
    private Long currencyId;
    private String name;
    private String description;
    private Long checkInTimeFrom;
    private Long checkInTimeTo;
    private Long checkOutTimeFrom;
    private Long checkOutTimeTo;

    private List<CreateUnitDto> units;
    private List<CreateAccommodationAmenityDto> amenities;
    private List<Long> languageIds;
    private List<CreateImageDto> images;
    private CreateLocationDto location;
}
