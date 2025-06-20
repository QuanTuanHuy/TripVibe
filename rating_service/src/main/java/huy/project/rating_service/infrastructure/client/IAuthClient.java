package huy.project.rating_service.infrastructure.client;

import huy.project.rating_service.core.domain.dto.request.LoginRequest;
import huy.project.rating_service.core.domain.dto.response.LoginResponse;
import huy.project.rating_service.ui.resource.Resource;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "auth-service",
        url = "${app.services.authentication_service.url}"
)
public interface IAuthClient {
    @PostMapping("/api/public/v1/auth/login")
    Resource<LoginResponse> login(@RequestBody LoginRequest request);
}
