package huy.project.rating_service.ui.controller;

import huy.project.rating_service.core.domain.dto.request.CreateRatingResponseDto;
import huy.project.rating_service.core.domain.entity.RatingResponseEntity;
import huy.project.rating_service.core.service.IRatingResponseService;
import huy.project.rating_service.kernel.utils.AuthenUtils;
import huy.project.rating_service.ui.resource.Resource;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/v1/rating_responses")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RatingResponseController {
    IRatingResponseService ratingResponseService;

    @PostMapping
    public ResponseEntity<Resource<RatingResponseEntity>> createRatingResponse(
            @RequestBody CreateRatingResponseDto req
    ) {
        Long userId = AuthenUtils.getCurrentUserId();
        return ResponseEntity.ok(new Resource<>(ratingResponseService.createRatingResponse(userId, req)));
    }

    @GetMapping
    public ResponseEntity<Resource<List<RatingResponseEntity>>> getAllRatings(
            @RequestParam(name = "ratingIds") List<Long> ratingIds
    ) {
        return ResponseEntity.ok(new Resource<>(ratingResponseService.getRatingResponsesByRatingIds(ratingIds)));
    }
}
