package huy.project.rating_service.ui.controller;

import huy.project.rating_service.core.domain.dto.response.RatingStatisticDto;
import huy.project.rating_service.core.service.IRatingService;
import huy.project.rating_service.ui.resource.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/v1/statistics")
@RequiredArgsConstructor
@Slf4j
public class RatingStatisticController {
    private final IRatingService ratingService;

    @GetMapping
    public ResponseEntity<Resource<RatingStatisticDto>> getStatisticsByAccId(
            @RequestParam Long accommodationId
    ) {
        return ResponseEntity.ok(new Resource<>(ratingService.getStatisticByAccId(accommodationId)));
    }
}
