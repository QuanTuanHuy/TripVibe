package huy.project.authentication_service.infrastructure.repository.model;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "role_privileges")
public class RolePrivilegeModel extends AuditTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "privilege_id")
    private Long privilegeId;
    @Column(name = "role_id")
    private Long roleId;
}
