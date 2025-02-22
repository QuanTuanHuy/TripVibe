package huy.project.authentication_service.core.domain.entity;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PrivilegeEntity {
    private Long id;
    private String name;
    private String description;
}
