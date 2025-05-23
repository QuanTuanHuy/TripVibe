package huy.project.inventory_service.infrastructure.repository.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomModel extends AuditTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unit_id")
    private Long unitId;
    
    @Column(name = "room_number", length = 20, nullable = false)
    private String roomNumber;
    
    @Column(name = "base_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal basePrice;
    
    @Column(name = "name", length = 100, nullable = false)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

}