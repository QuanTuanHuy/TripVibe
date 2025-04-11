package huy.project.authentication_service.core.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateNotificationDto {
    private Long userId;
    private String type;
    private String title;
    private String content;
    private String recipient;
}
