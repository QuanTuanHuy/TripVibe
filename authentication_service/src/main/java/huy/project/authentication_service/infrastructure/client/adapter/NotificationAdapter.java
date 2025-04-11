package huy.project.authentication_service.infrastructure.client.adapter;

import huy.project.authentication_service.core.domain.dto.OtpDto;
import huy.project.authentication_service.core.port.INotificationPort;
import huy.project.authentication_service.infrastructure.client.INotificationClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationAdapter implements INotificationPort {
//    INotificationClient notificationClient;

    @Override
    public void sendOtp(OtpDto otp) {

    }
}
