package huy.project.inventory_service.core.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SyncUnitDto {
    private Long accommodationId;
    private Long unitId;
    private Long unitNameId;
    private BigDecimal basePrice;
    private String unitName;
    private Integer quantity;
}
