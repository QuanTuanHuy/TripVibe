package huy.project.accommodation_service.core.domain.entity;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UnitEntity {
    private Long id;
    private Long accommodationId;
    private Long unitNameId;
    private String description;
    private BigDecimal pricePerNight;
    private Long maxGuest;
    private Boolean useSharedBathroom;
    private Boolean isDeleted;
    private Integer quantity;

    private UnitNameEntity unitName;
    private List<ImageEntity> images;
    private List<BedroomEntity> bedrooms;
    private List<UnitAmenityEntity> amenities;
    private List<UnitPriceTypeEntity> priceTypes;
    private List<UnitPriceGroupEntity> priceGroups;
}
