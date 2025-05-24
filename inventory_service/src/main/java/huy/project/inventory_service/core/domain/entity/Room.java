package huy.project.inventory_service.core.domain.entity;

import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Room {
    private Long id;

    private Long unitId;

    private String roomNumber;

    private BigDecimal basePrice;

    private String name;

    private String description;
}
