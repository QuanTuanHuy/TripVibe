package huy.project.profile_service.infrastructure.client;

import huy.project.profile_service.core.domain.dto.response.AccommodationDto;
import huy.project.profile_service.kernel.config.FeignClientConfig;
import huy.project.profile_service.ui.resource.Resource;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "accommodation-service",
        url = "${app.services.accommodation_service.url}",
        configuration = FeignClientConfig.class
)
public interface IAccommodationClient {
    @GetMapping("/api/internal/v1/accommodations")
    Resource<List<AccommodationDto>> getAccommodations(@RequestParam(name = "ids") List<Long> ids);
}
