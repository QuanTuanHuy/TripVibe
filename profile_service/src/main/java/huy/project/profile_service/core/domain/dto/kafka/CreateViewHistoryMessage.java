package huy.project.profile_service.core.domain.dto.kafka;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CreateViewHistoryMessage {
    private Long touristId;
    private Long accommodationId;
    private Long timestamp;
}
