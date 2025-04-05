package huy.project.profile_service.core.domain.entity;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CreditCardEntity {
    private Long id;
    private String cardNumber;
    private Long expirationDate;
    private String name;
}
