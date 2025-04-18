package huy.project.search_service.core.domain.dto.kafka;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SyncRatingMessage {
    private Long accommodationId;
    private Double rating;
}
