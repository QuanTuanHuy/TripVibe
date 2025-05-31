package huy.project.rating_service.ui.controller;

import huy.project.rating_service.core.domain.dto.request.PendingReviewParams;
import huy.project.rating_service.core.domain.dto.response.ListDataResponse;
import huy.project.rating_service.core.domain.dto.response.PendingReviewDto;
import huy.project.rating_service.core.domain.entity.PendingReviewEntity;
import huy.project.rating_service.core.service.IPendingReviewService;
import huy.project.rating_service.kernel.utils.AuthenUtils;
import huy.project.rating_service.ui.resource.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/v1/pending_reviews")
@RequiredArgsConstructor
@Slf4j
public class PendingReviewController {
    public static final String DEFAULT_PAGE = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";

    private final IPendingReviewService pendingReviewService;

    @PostMapping
    public ResponseEntity<Resource<PendingReviewEntity>> createPendingReview(
            @RequestBody PendingReviewEntity pendingReview
    ) {
        Long userId = AuthenUtils.getCurrentUserId();
        pendingReview.setUserId(userId);
        var createdPendingReview = pendingReviewService.createPendingReview(pendingReview);
        return ResponseEntity.ok(new Resource<>(createdPendingReview));
    }

    @GetMapping
    public ResponseEntity<Resource<ListDataResponse<PendingReviewDto>>> getPendingReviews(
            @RequestParam(name = "page", defaultValue = DEFAULT_PAGE) Integer page,
            @RequestParam(name = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) Integer pageSize,
            @RequestParam(name = "sortBy", required = false) String sortBy,
            @RequestParam(name = "sortType", required = false) String sortType
    ) {
        Long userId = AuthenUtils.getCurrentUserId();
        var params = PendingReviewParams.builder()
                .userId(userId)
                .page(page).pageSize(pageSize)
                .sortBy(sortBy).sortType(sortType)
                .build();
        var result = pendingReviewService.getPendingReviews(params);

        return ResponseEntity.ok(
                new Resource<>(ListDataResponse.from(result.getFirst(), result.getSecond()))
        );
    }

    @DeleteMapping
    public ResponseEntity<Resource<Void>> deletePendingReviews(
            @RequestParam(name = "ids") List<Long> ids
    ) {
        Long userId = AuthenUtils.getCurrentUserId();
        pendingReviewService.deletePendingReviews(userId, ids);
        return ResponseEntity.ok(new Resource<>(null));
    }
}
