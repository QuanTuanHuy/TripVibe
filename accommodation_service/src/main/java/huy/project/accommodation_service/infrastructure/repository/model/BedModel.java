package huy.project.accommodation_service.infrastructure.repository.model;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "beds")
public class BedModel extends AuditTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bedroom_id")
    private Long bedroomId;

    @Column(name = "bed_type_id")
    private Long bedTypeId;

    @Column(name = "quantity")
    private Integer quantity;
}
