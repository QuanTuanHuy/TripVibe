package huy.project.rating_service.ui.controller;

import huy.project.rating_service.core.domain.dto.CreateRatingDto;
import huy.project.rating_service.core.domain.entity.RatingEntity;
import huy.project.rating_service.core.service.IRatingService;
import huy.project.rating_service.kernel.utils.AuthenUtils;
import huy.project.rating_service.ui.resource.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/v1/ratings")
@RequiredArgsConstructor
@Slf4j
public class RatingController {
    private final IRatingService ratingService;

    @PostMapping
    public ResponseEntity<Resource<RatingEntity>> createRating(
            @RequestBody CreateRatingDto req
    ) {
        Long userId = AuthenUtils.getCurrentUserId();
        req.setUserId(userId);
        return ResponseEntity.ok(new Resource<>(ratingService.createRating(req)));
    }
}
