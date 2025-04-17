package huy.project.rating_service.infrastructure.repository.model;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "monthly_ratings")
public class MonthlyRatingModel extends AuditTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "year_month")
    private String yearMonth;
    @Column(name = "rating_count")
    private Integer ratingCount;
    @Column(name = "total_rating")
    private Long totalRating;
    @Column(name = "rating_trend_id")
    private Long ratingTrendId;
}
