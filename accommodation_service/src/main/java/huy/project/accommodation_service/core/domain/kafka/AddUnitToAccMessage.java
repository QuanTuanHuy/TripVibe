package huy.project.accommodation_service.core.domain.kafka;

import huy.project.accommodation_service.core.domain.constant.TopicConstant;
import huy.project.accommodation_service.core.domain.entity.UnitEntity;
import lombok.*;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AddUnitToAccMessage {
    private Long accommodationId;
    private CreateUnitMessage unit;

    public KafkaBaseDto<AddUnitToAccMessage> toKafkaBaseDto() {
        return KafkaBaseDto.<AddUnitToAccMessage>builder()
                .cmd(TopicConstant.BookingCommand.ADD_UNIT_TO_ACC)
                .timestamp(Instant.now().toEpochMilli())
                .data(this)
                .build();
    }

    public static AddUnitToAccMessage from(UnitEntity unit) {
        return AddUnitToAccMessage.builder()
                .accommodationId(unit.getAccommodationId())
                .unit(CreateUnitMessage.builder()
                        .id(unit.getId())
                        .name(unit.getUnitName().getName())
                        .build())
                .build();
    }
}
