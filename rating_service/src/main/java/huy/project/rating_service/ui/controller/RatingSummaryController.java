package huy.project.rating_service.ui.controller;

import huy.project.rating_service.core.domain.entity.RatingSummaryEntity;
import huy.project.rating_service.core.service.IRatingSummaryService;
import huy.project.rating_service.ui.resource.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/v1/rating_summaries")
@RequiredArgsConstructor
@Slf4j
public class RatingSummaryController {
    private final IRatingSummaryService ratingSummaryService;

    @GetMapping
    public Resource<List<RatingSummaryEntity>> getSummariesByAccIds(
            @RequestParam(name = "accommodationIds") List<Long> accIds
    ) {
        return new Resource<>(ratingSummaryService.getRatingSummariesByAccIds(accIds));
    }
}
