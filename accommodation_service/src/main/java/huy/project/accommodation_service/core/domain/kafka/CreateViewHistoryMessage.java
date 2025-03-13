package huy.project.accommodation_service.core.domain.kafka;

import huy.project.accommodation_service.core.domain.constant.TopicConstant;
import lombok.*;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CreateViewHistoryMessage {
    private Long touristId;
    private Long accommodationId;
    private Long timestamp;

    public KafkaBaseDto<CreateViewHistoryMessage> toKafkaBaseDto() {
        return KafkaBaseDto.<CreateViewHistoryMessage>builder()
                .cmd(TopicConstant.ViewHistoryCommand.CREATE_VIEW_HISTORY)
                .data(this)
                .timestamp(Instant.now().toEpochMilli())
                .build();
    }
}
