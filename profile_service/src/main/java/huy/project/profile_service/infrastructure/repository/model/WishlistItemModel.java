package huy.project.profile_service.infrastructure.repository.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "wishlist_items")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WishlistItemModel extends AuditTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "wishlist_id", nullable = false)
    Long wishlistId;

    @Column(name = "accommodation_id", nullable = false)
    Long accommodationId;

    @Column(name = "accommodation_name", nullable = false)
    String accommodationName;

    @Column(name = "accommodation_image_url", nullable = false)
    String accommodationImageUrl;
}
