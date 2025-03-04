package huy.project.accommodation_service.infrastructure.repository.model;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "bedrooms")
public class BedroomModel extends AuditTable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unit_id")
    private Long unitId;

    @Column(name = "quantity")
    private Integer quantity;
}
