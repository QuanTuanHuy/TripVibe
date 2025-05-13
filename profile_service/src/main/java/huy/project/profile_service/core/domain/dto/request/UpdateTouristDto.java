package huy.project.profile_service.core.domain.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class UpdateTouristDto {
    @Size(max = 100, message = "Tên không được vượt quá 100 ký tự")
    private String name;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Số điện thoại không hợp lệ")
    private String phone;

    @Size(max = 255, message = "URL avatar không được vượt quá 255 ký tự")
    private String avatarUrl;

    private LocalDate dateOfBirth;

    @Size(max = 20, message = "Giới tính không được vượt quá 20 ký tự")
    private String gender;

    @Valid
    private UpdateLocationDto location;

    @Valid
    private UpdatePassportDto passport;

    @Valid
    private UpdateUserSettingDto userSetting;
}
