package huy.project.search_service.core.domain.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BedEntity {
    private Long id;
    private Integer quantity;
    private Long bedTypeId;
}
