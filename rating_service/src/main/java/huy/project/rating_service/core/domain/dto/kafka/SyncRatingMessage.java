package huy.project.rating_service.core.domain.dto.kafka;

import huy.project.rating_service.core.domain.constant.TopicConstant;
import lombok.*;

import java.time.Instant;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SyncRatingMessage {
    private Long accommodationId;
    private Double rating;

    public static KafkaBaseDto<List<SyncRatingMessage>> toKafkaDto(List<SyncRatingMessage> messages) {
        return KafkaBaseDto.<List<SyncRatingMessage>>builder()
                .cmd(TopicConstant.SearchCommand.SYNC_RATING_SUMMARY)
                .timestamp(Instant.now().toEpochMilli())
                .data(messages)
                .build();
    }
}
