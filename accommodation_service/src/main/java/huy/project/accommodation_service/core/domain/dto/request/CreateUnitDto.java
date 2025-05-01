package huy.project.accommodation_service.core.domain.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class CreateUnitDto {
    private Long unitNameId;
    private String description;
    private BigDecimal pricePerNight;
    private Integer maxAdults;
    private Integer maxChildren;
    private Boolean useSharedBathroom;

    private List<CreateUnitPriceTypeDto> priceTypes;
    private List<CreateUnitPriceGroup> priceGroups;
    private List<CreateImageDto> images;
    private List<CreateUnitAmenityDto> amenities;
    private List<CreateBedroomDto> bedrooms;
}
