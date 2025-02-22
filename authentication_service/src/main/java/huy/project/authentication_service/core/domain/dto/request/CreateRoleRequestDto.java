package huy.project.authentication_service.core.domain.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRoleRequestDto {
    private String name;
    private String description;
    private List<Long> privilegeIds;
}
