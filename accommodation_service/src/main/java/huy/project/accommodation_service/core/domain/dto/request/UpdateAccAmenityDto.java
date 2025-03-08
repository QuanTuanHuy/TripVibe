package huy.project.accommodation_service.core.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateAccAmenityDto {
    private List<CreateAccommodationAmenityDto> newAmenities;
    // id of amenities, not the id of AccommodationAmenityEntity
    private List<Long> deleteAmenityIds;
}
