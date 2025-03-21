package huy.project.search_service.core.domain.dto.kafka;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class DomainEventDto<T> {
    private String eventId;
    private String eventType;
    private Long timestamp;
    private T data;
}
