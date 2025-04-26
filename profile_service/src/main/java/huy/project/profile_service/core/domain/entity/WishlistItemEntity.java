package huy.project.profile_service.core.domain.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults( level = AccessLevel.PRIVATE )
public class WishlistItemEntity {
    Long id;
    Long wishlistId;
    Long accommodationId;
    String accommodationName;
    String accommodationImageUrl;
    LocalDateTime createdAt;
}
