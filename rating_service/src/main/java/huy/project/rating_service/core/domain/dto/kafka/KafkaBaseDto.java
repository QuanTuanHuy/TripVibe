package huy.project.rating_service.core.domain.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

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