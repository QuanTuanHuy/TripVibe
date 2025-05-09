package huy.project.rating_service.ui.controller;

import huy.project.rating_service.core.domain.dto.response.RatingSummaryDto;
import huy.project.rating_service.core.domain.entity.RatingSummaryEntity;
import huy.project.rating_service.core.service.IRatingSummaryService;
import huy.project.rating_service.ui.resource.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/public/v1/rating_summaries")
@RequiredArgsConstructor
@Slf4j
public class RatingSummaryController {
    private final IRatingSummaryService ratingSummaryService;

    // only for test
    @PostMapping
    public ResponseEntity<Resource<?>> createRatingSummary(
            @RequestParam Long accommodationId
    ) {
        var distribution = new HashMap<Integer, Integer>() {{
            put(1, 0);
            put(2, 0);
            put(3, 0);
            put(4, 0);
            put(5, 0);
            put(6, 0);
            put(7, 0);
            put(8, 0);
            put(9, 0);
            put(10, 0);
        }};

        var ratingSummary = RatingSummaryEntity.builder()
                .accommodationId(accommodationId)
                .isSyncedWithSearchService(false)
                .numberOfRatings(0)
                .totalRating(0L)
                .distribution(distribution)
                .build();
        ratingSummaryService.createRatingSummary(ratingSummary);
        return ResponseEntity.ok(new Resource<>(null));
    }

    @GetMapping
    public ResponseEntity<Resource<List<RatingSummaryDto>>> getSummariesByAccIds(
            @RequestParam(name = "accommodationIds") List<Long> accIds
    ) {
        var response = ratingSummaryService.getRatingSummariesByAccIds(accIds).stream()
                .map(rs -> RatingSummaryDto.builder()
                        .accommodationId(rs.getId())
                        .numberOfRatings(rs.getNumberOfRatings())
                        .totalRating(rs.getTotalRating())
                        .build())
                .toList();
        return ResponseEntity.ok(new Resource<>(response));
    }
}
