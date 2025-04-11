package huy.project.authentication_service.core.port;

import huy.project.authentication_service.core.domain.dto.OtpDto;

public interface INotificationPort {
    void sendOtp(OtpDto otp);
}
