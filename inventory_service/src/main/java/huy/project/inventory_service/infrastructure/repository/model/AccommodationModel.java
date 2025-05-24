package huy.project.inventory_service.infrastructure.repository.model;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "accommodations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccommodationModel extends AuditTable {
    @Id
    private Long id;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "address")
    private String address;

}