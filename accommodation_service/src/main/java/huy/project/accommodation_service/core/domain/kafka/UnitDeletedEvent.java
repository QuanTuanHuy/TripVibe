package huy.project.accommodation_service.core.domain.kafka;

import huy.project.accommodation_service.core.domain.constant.TopicConstant;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UnitDeletedEvent {
    private Long accommodationId;
    private Long unitId;

    public DomainEventDto<UnitDeletedEvent> toDomainEventDto() {
        return DomainEventDto.<UnitDeletedEvent>builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(TopicConstant.AccommodationEvent.DELETE_UNIT)
                .timestamp(Instant.now().toEpochMilli())
                .data(this)
                .build();
    }
}
