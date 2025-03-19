package huy.project.accommodation_service.core.domain.kafka;

import huy.project.accommodation_service.core.domain.entity.BedEntity;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CreateBedElasticMessage {
    private Long id;
    private Integer quantity;
    private Long bedTypeId;

    public static CreateBedElasticMessage from(BedEntity bed) {
        return CreateBedElasticMessage.builder()
                .id(bed.getId())
                .quantity(bed.getQuantity())
                .bedTypeId(bed.getBedTypeId())
                .build();
    }
}
