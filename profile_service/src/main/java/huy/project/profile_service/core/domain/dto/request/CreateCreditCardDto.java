package huy.project.profile_service.core.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCreditCardDto {
    private String cardNumber;
    private Long expirationDate;
    private String name;
}
