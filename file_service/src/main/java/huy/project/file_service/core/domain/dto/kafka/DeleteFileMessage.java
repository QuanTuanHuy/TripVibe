package huy.project.file_service.core.domain.dto.kafka;

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
}
