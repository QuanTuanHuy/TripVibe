package huy.project.profile_service.core.domain.entity;

import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TouristEntity {
    private Long id;
    private String name;
    private String gender;
    private String phone;
    private String email;
    private String avatarUrl;
    private LocalDate dateOfBirth;
    private Long locationId;
    private Long passportId;
    private Long creditCardId;
    private Integer memberLevel;
    private Boolean isActive;

    private LocationEntity location;
    private PassportEntity passport;
    private CreditCardEntity creditCard;
}
