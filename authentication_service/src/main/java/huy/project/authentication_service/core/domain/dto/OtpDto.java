package huy.project.authentication_service.core.domain.dto;

import huy.project.authentication_service.core.domain.constant.OtpType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpDto {
    private String otp;
    private OtpType type;
    private Long expiredAt;
}
