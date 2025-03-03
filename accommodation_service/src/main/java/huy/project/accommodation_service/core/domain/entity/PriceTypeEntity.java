package huy.project.accommodation_service.core.domain.entity;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PriceTypeEntity {
    private Long id;
    private String name;
    private String description;
}
