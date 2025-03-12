package huy.project.profile_service.core.domain.entity;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PassportEntity {
    private Long id;
    private String passportNumber;
    private String firstName;
    private String lastName;
    private Long expirationDate;
    private Long nationalityId;
}
