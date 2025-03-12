package huy.project.profile_service.core.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePassportDto {
    private String passportNumber;
    private String firstName;
    private String lastName;
    private Long expirationDate;
    private Long nationalityId;
}
