package huy.project.rating_service.infrastructure.repository.model;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "rating_helpfulnesses")
public class RatingHelpfulnessModel extends AuditTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "rating_id")
    private Long ratingId;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "is_helpful")
    private Boolean isHelpful;
}
