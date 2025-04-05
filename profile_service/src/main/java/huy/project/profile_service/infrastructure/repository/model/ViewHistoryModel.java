package huy.project.profile_service.infrastructure.repository.model;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "view_histories")
public class ViewHistoryModel extends AuditTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "tourist_id")
    private Long touristId;
    @Column(name = "accommodation_id")
    private Long accommodationId;
    @Column(name = "timestamp")
    private Long timestamp;
}
