package huy.project.profile_service.core.domain.entity;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ViewHistoryEntity {
    private Long id;
    private Long touristId;
    private Long accommodationId;
    private Long timestamp;
}
