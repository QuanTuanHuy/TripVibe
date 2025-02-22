package huy.project.authentication_service.core.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateRoleRequestDto {
    private String name;
    private String description;
    private List<Long> privilegeIds;
}
