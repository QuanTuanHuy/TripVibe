package huy.project.accommodation_service.core.domain.kafka;

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
