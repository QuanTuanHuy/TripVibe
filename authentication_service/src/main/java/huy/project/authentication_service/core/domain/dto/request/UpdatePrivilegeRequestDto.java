package huy.project.authentication_service.core.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePrivilegeRequestDto {
    private String name;
    private String description;
}
