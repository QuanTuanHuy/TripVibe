package huy.project.accommodation_service.core.domain.kafka;

import huy.project.accommodation_service.core.domain.constant.TopicConstant;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class DeleteFileMessage {
    private Long userId;
    private List<Long> ids;

    public KafkaBaseDto<DeleteFileMessage> toKafkaBaseDto() {
        return KafkaBaseDto.<DeleteFileMessage>builder()
                .cmd(TopicConstant.FileCommand.DELETE_FILE)
                .data(this)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}
