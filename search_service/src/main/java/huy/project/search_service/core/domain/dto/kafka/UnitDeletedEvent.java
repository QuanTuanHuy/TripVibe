package huy.project.search_service.core.domain.dto.kafka;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

import huy.project.search_service.core.domain.constant.TopicConstant;

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
