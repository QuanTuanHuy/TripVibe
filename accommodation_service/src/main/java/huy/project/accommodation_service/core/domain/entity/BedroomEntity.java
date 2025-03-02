package huy.project.accommodation_service.core.domain.entity;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class BedroomEntity {
    private Long id;
    private Long unitId;
    private Integer quantity;

    private List<BedEntity> beds;
}
