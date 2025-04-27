package huy.project.profile_service.infrastructure.repository.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "wishlists")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WishlistModel extends AuditTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "tourist_id", nullable = false)
    Long touristId;

    @Column(name = "name", nullable = false)
    String name;
}
