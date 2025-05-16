package huy.project.inventory_service.core.domain.entity;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Unit {
    private Long id;

    private Long accommodationId;

    private Long unitNameId;

    private String unitName;

    private BigDecimal basePrice;

    private Integer quantity;

    private List<Room> rooms;
}
