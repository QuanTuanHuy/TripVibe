package huy.project.accommodation_service.core.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateUnitAmenityDto {
    private List<CreateUnitAmenityDto> newAmenities;
    // id of amenities not the id of unit amenity
    private List<Long> deletedAmenityIds;
}
