package huy.project.profile_service.core.domain.dto.kafka;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CreateTouristMessage {
    private Long userId;
    private String email;
}
