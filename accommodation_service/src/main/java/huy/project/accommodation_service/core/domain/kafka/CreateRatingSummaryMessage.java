package huy.project.accommodation_service.core.domain.kafka;

import huy.project.accommodation_service.core.domain.constant.TopicConstant;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CreateRatingSummaryMessage {
    private Long accommodationId;

    public KafkaBaseDto<CreateRatingSummaryMessage> toKafkaBaseDto() {
        return KafkaBaseDto.<CreateRatingSummaryMessage>builder()
                .cmd(TopicConstant.RatingCommand.CREATE_RATING_SUMMARY)
                .data(this)
                .build();
    }
}
