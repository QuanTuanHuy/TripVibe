package huy.project.inventory_service.infrastructure.repository.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "units")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnitModel extends AuditTable {
    @Id
    private Long id;

    @Column(name = "accommodation_id")
    private Long accommodationId;
    
    @Column(name = "unit_name_id")
    private Long unitNameId;
    
    @Column(name = "unit_name", length = 255)
    private String unitName;
    
    @Column(name = "base_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal basePrice;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
}