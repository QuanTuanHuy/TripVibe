package huy.project.accommodation_service.core.domain.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreateAccommodationDtoV2 {
    private Long typeId;
    private Long currencyId;
    private String name;
    private String description;
    private Long checkInTimeFrom;
    private Long checkInTimeTo;
    private Long checkOutTimeFrom;
    private Long checkOutTimeTo;

    private CreateUnitDtoV2 unit;
    private List<Long> amenityIds;
    private List<Long> languageIds;
    private CreateLocationDto location;
}
