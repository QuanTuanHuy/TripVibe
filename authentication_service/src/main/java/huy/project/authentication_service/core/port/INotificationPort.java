package huy.project.authentication_service.core.port;

import huy.project.authentication_service.core.domain.dto.request.CreateNotificationDto;

public interface INotificationPort {
    void createNotification(CreateNotificationDto req);
}
