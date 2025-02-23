package huy.project.authentication_service.core.domain.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequestDto {
    private String name;
    private String username;
    private String email;
    private String password;
    private List<Long> roleIds;
}
