package huy.project.rating_service.infrastructure.repository.model;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "rating_responses")
public class RatingResponseModel extends AuditTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "rating_id")
    private Long ratingId;
    @Column(name = "owner_id")
    private Long ownerId;
    @Column(name = "content")
    private String content;
}
