package huy.project.accommodation_service.core.domain.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CreateUnitAmenityDto {
    private Long amenityId;
    private Long fee;
    private Boolean needToReserve;
}
