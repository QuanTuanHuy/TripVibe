package huy.project.rating_service.core.domain.dto.kafka;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CreateRatingSummaryMessage {
    private Long accommodationId;
}
