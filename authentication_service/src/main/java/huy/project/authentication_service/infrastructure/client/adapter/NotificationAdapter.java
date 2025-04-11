package huy.project.authentication_service.infrastructure.client.adapter;

import huy.project.authentication_service.core.domain.dto.request.CreateNotificationDto;
import huy.project.authentication_service.core.port.INotificationPort;
import huy.project.authentication_service.infrastructure.client.INotificationClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class NotificationAdapter implements INotificationPort {
    INotificationClient notificationClient;

    @Override
    public void createNotification(CreateNotificationDto req) {
        try {
            var response = notificationClient.createNotification(req);
            if (!response.getMeta().getMessage().equals("Success")) {
                log.info("send notification failed, {}", response.getMeta().getMessage());
            }
        } catch (Exception e) {
            log.error("error occurred while sending notification, {}", e.getMessage());
        }
    }
}
