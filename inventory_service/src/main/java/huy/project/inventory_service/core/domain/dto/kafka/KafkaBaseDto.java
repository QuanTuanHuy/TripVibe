package huy.project.inventory_service.core.domain.dto.kafka;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class KafkaBaseDto<T> {
    private String cmd;
    private String errorCode;
    private String errorMessage;
    private Long timestamp;
    private T data;
}
