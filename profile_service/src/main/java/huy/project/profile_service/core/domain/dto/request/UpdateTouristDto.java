package huy.project.profile_service.core.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class UpdateTouristDto {
    private String name;
    private String phone;
    private String avatarUrl;
    private LocalDate dateOfBirth;
    private String gender;
    private UpdateLocationDto location;
    private UpdatePassportDto passport;
    private UpdateUserSettingDto userSetting;
}
