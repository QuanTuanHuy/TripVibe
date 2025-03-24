package huy.project.rating_service.infrastructure.client;

import huy.project.rating_service.core.domain.dto.response.UserProfileDto;
import huy.project.rating_service.kernel.config.FeignClientConfig;
import huy.project.rating_service.ui.resource.Resource;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "profile-service",
        url = "${app.services.profile_service.url}",
        configuration = FeignClientConfig.class
)
public interface IUserProfileClient {
    @GetMapping("/api/internal/v1/profiles")
    Resource<List<UserProfileDto>> getUserProfilesByIds(
            @RequestParam("touristIds") List<Long> userIds
    );
}
