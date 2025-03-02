package huy.project.accommodation_service.core.domain.entity;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class BedEntity {
    private Long id;
    private Long bedroomId;
    private Long bedTypeId;
    private Integer quantity;

    private BedTypeEntity type;
}
