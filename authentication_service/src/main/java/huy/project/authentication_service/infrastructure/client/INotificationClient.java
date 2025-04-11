package huy.project.authentication_service.infrastructure.client;

import huy.project.authentication_service.core.domain.dto.request.CreateNotificationDto;
import huy.project.authentication_service.ui.resource.Resource;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "notification-client",
        url = "${app.services.notification_service.url}"
)
public interface INotificationClient {
    @PostMapping("api/public/v1/notifications")
    Resource<?> createNotification(@RequestBody CreateNotificationDto req);
}
