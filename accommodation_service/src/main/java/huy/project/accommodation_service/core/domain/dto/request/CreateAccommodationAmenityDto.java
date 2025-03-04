package huy.project.accommodation_service.core.domain.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateAccommodationAmenityDto {
    private Long amenityId;
    private Long fee;
    private Boolean needToReserve;
}
