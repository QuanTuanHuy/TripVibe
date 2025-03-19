package huy.project.accommodation_service.core.domain.kafka;

import huy.project.accommodation_service.core.domain.constant.TopicConstant;
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
}
