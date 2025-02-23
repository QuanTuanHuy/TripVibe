package huy.project.authentication_service.core.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateUserRequestDto {
    private String name;
    private String username;
    private String email;
    private String password;
    private List<Long> roleIds;
}
