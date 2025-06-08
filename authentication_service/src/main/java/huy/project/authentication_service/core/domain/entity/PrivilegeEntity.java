package huy.project.authentication_service.core.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import huy.project.authentication_service.core.domain.constant.PrivilegeType;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PrivilegeEntity {
    private Long id;
    private String name;
    private String description;

    @JsonIgnore
    public PrivilegeType getPrivilegeType() {
        return PrivilegeType.fromPrivilegeName(this.name);
    }

    @JsonIgnore
    public String getCategory() {
        PrivilegeType type = getPrivilegeType();
        return type != null ? type.getCategory() : "UNKNOWN";
    }
}
