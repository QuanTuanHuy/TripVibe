package huy.project.accommodation_service.core.domain.kafka;

import huy.project.accommodation_service.core.domain.entity.UnitAmenityEntity;
import huy.project.accommodation_service.core.domain.entity.UnitEntity;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CreateUnitElasticMessage {
    private Long id;
    private Integer maxAdults;
    private Integer maxChildren;
    private BigDecimal pricePerNight;
    private Integer quantity;

    private List<Long> amenityIds;
    private List<CreateBedroomElasticMessage> bedrooms;

    public static CreateUnitElasticMessage from(UnitEntity unit) {
        List<Long> amenityIds = unit.getAmenities().stream().map(UnitAmenityEntity::getAmenityId).toList();
        return CreateUnitElasticMessage.builder()
                .id(unit.getId())
                .maxAdults(unit.getMaxGuest().intValue())
                .maxChildren(0)
                .pricePerNight(unit.getPricePerNight())
                .quantity(unit.getQuantity())
                .amenityIds(amenityIds)
                .bedrooms(unit.getBedrooms().stream().map(CreateBedroomElasticMessage::from).toList())
                .build();
    }
}
