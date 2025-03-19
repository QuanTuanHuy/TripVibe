package huy.project.accommodation_service.core.domain.kafka;

import huy.project.accommodation_service.core.domain.constant.TopicConstant;
import lombok.*;

import java.time.Instant;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CreateAccommodationMessage {
    private Long id;
    private String name;
    private Long ownerId;
    private List<CreateUnitMessage> units;

    public KafkaBaseDto<CreateAccommodationMessage> toKafkaBaseDto() {
        return KafkaBaseDto.<CreateAccommodationMessage>builder()
                .cmd(TopicConstant.BookingCommand.CREATE_ACCOMMODATION)
                .timestamp(Instant.now().toEpochMilli())
                .data(this)
                .build();
    }
}
