package huy.project.profile_service.core.domain.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults( level = AccessLevel.PRIVATE )
public class WishlistEntity {
    Long id;
    Long touristId;
    String name;
    LocalDateTime createdAt;

    List<WishlistItemEntity> items;
}
