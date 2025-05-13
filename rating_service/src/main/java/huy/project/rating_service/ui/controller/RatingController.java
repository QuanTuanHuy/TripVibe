package huy.project.rating_service.ui.controller;

import huy.project.rating_service.core.domain.dto.request.CreateRatingDto;
import huy.project.rating_service.core.domain.dto.request.CreateRatingHelpfulnessDto;
import huy.project.rating_service.core.domain.dto.request.RatingParams;
import huy.project.rating_service.core.domain.dto.response.ListDataResponse;
import huy.project.rating_service.core.domain.dto.response.RatingDto;
import huy.project.rating_service.core.domain.entity.RatingEntity;
import huy.project.rating_service.core.domain.entity.RatingHelpfulnessEntity;
import huy.project.rating_service.core.service.IRatingService;
import huy.project.rating_service.kernel.utils.AuthenUtils;
import huy.project.rating_service.ui.resource.Resource;
import jakarta.validation.Valid;
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

    public static final String DEFAULT_PAGE = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";

    @PostMapping
    public ResponseEntity<Resource<RatingEntity>> createRating(
            @Valid @RequestBody CreateRatingDto req
    ) {
        Long userId = AuthenUtils.getCurrentUserId();
        req.setUserId(userId);
        return ResponseEntity.ok(new Resource<>(ratingService.createRating(req)));
    }

    @PostMapping("/{id}/helpfulness")
    public ResponseEntity<Resource<RatingHelpfulnessEntity>> createRatingHelpfulness(
            @PathVariable Long id,
            @Valid @RequestBody CreateRatingHelpfulnessDto req
    ) {
        Long userId = AuthenUtils.getCurrentUserId();
        req.setRatingId(id);
        var ratingHelpfulness = ratingService.createRatingHelpfulness(userId, req);
        return ResponseEntity.ok(new Resource<>(ratingHelpfulness));
    }

    @GetMapping
    public ResponseEntity<Resource<ListDataResponse<RatingDto>>> getAllRatings(
            @RequestParam(name = "page", defaultValue = DEFAULT_PAGE) Integer page,
            @RequestParam(name = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) Integer pageSize,
            @RequestParam(name = "sortBy", required = false) String sortBy,
            @RequestParam(name = "sortType", required = false) String sortType,
            @RequestParam(name = "unitId", required = false) Long unitId,
            @RequestParam(name = "accommodationId") Long accommodationId,
            @RequestParam(name = "minValue", required = false) Double minValue,
            @RequestParam(name = "maxValue", required = false) Double maxValue,
            @RequestParam(name = "languageId", required = false) Long languageId,
            @RequestParam(name = "createdFrom", required = false) Long createdFrom,
            @RequestParam(name = "createdTo", required = false) Long createdTo
    ) {
        var params = RatingParams.builder()
                .page(page).pageSize(pageSize)
                .sortBy(sortBy).sortType(sortType)
                .unitId(unitId).accommodationId(accommodationId)
                .minValue(minValue).maxValue(maxValue)
                .languageId(languageId)
                .createdFrom(createdFrom).createdTo(createdTo)
                .build();
        var result = ratingService.getAllRatings(params);
        return ResponseEntity.ok(new Resource<>(ListDataResponse.from(result.getFirst(), result.getSecond())));
    }
}
