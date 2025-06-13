package huy.project.search_service.infrastructure.client;

import huy.project.search_service.core.domain.dto.response.AccommodationThumbnail;
import huy.project.search_service.ui.resource.Resource;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@FeignClient(
        name = "accommodation-service",
        url = "${app.services.accommodation_service.url}"
)
public interface IAccommodationClient {
    @GetMapping("/api/public/v1/accommodations/thumbnails")
    Resource<List<AccommodationThumbnail>> getAccommodationsThumbnail(
            @RequestParam(name = "ids") List<Long> ids,
            @RequestParam(name = "startDate") String startDate,
            @RequestParam(name = "endDate") String endDate,
            @RequestParam(name = "guestCount") Integer guestCount
    );
}
