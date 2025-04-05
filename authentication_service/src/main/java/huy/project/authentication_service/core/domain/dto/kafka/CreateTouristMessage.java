package huy.project.authentication_service.core.domain.dto.kafka;

import huy.project.authentication_service.core.domain.constant.TopicConstant;
import lombok.*;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CreateTouristMessage {
    private Long userId;
    private String email;

    public KafkaBaseDto<CreateTouristMessage> toKafkaBaseDto() {
        return KafkaBaseDto.<CreateTouristMessage>builder()
                .cmd(TopicConstant.TouristCommand.CREATE_TOURIST)
                .timestamp(Instant.now().toEpochMilli())
                .data(this)
                .build();
    }
}
