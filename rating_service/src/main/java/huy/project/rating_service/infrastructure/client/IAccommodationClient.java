package huy.project.rating_service.infrastructure.client;

import huy.project.rating_service.core.domain.dto.response.AccThumbnailDto;
import huy.project.rating_service.core.domain.dto.response.AccommodationDto;
import huy.project.rating_service.core.domain.dto.response.UnitDto;
import huy.project.rating_service.kernel.config.FeignClientConfig;
import huy.project.rating_service.ui.resource.Resource;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "accommodation-service",
        url = "${app.services.accommodation_service.url}",
        configuration = FeignClientConfig.class
)
public interface IAccommodationClient {
    @GetMapping("/api/internal/v1/units")
    Resource<List<UnitDto>> getUnitsByIds(
            @RequestParam("unitIds") List<Long> unitIds
    );

    @GetMapping("/api/internal/v1/accommodations/{id}")
    Resource<AccommodationDto> getAccommodationById(@PathVariable Long id);

    @GetMapping("/api/public/v1/accommodations/thumbnails")
    Resource<List<AccThumbnailDto>> getAccThumbnails(
            @RequestParam("ids") List<Long> ids
    );
}
