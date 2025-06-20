package huy.project.rating_service.core.domain.dto.request;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class LoginRequest {
    private String email;
    private String password;
}
