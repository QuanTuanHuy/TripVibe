package huy.project.accommodation_service.infrastructure.client;

import huy.project.accommodation_service.core.domain.dto.response.RatingSummaryDto;
import huy.project.accommodation_service.ui.resource.Resource;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "rating-service",
        url = "${app.services.rating_service.url}"
)
public interface IRatingClient {
    @GetMapping("/api/public/v1/rating_summaries")
    Resource<List<RatingSummaryDto>> getRatingSummariesByAccIds(
            @RequestParam(name = "accommodationIds") List<Long> accommodationIds
    );
}
