package huy.project.rating_service.infrastructure.repository.model;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "pending_reviews")
public class PendingReviewModel extends AuditTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "accommodation_id")
    private Long accommodationId;

    @Column(name = "unit_id")
    private Long unitId;

    @Column(name = "booking_id")
    private Long bookingId;
}
