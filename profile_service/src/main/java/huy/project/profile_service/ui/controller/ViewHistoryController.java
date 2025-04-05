package huy.project.profile_service.ui.controller;

import huy.project.profile_service.core.domain.dto.request.ViewHistoryParams;
import huy.project.profile_service.core.domain.dto.response.ListDataResponse;
import huy.project.profile_service.core.domain.entity.ViewHistoryEntity;
import huy.project.profile_service.core.service.IViewHistoryService;
import huy.project.profile_service.kernel.utils.AuthenUtils;
import huy.project.profile_service.ui.resource.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/v1/view_histories")
@RequiredArgsConstructor
public class ViewHistoryController {
    private final IViewHistoryService viewHistoryService;

    public static final String DEFAULT_PAGE = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";

    @GetMapping
    public ResponseEntity<Resource<ListDataResponse<ViewHistoryEntity>>> getViewHistories(
            @RequestParam(name = "page", defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(name = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize,
            @RequestParam(name = "sortBy", defaultValue = "timestamp") String sortBy
    ) {
        Long touristId = AuthenUtils.getCurrentUserId();
        ViewHistoryParams params = ViewHistoryParams.builder()
                .page(page).pageSize(pageSize)
                .sortBy(sortBy)
                .touristId(touristId)
                .build();
        var result = viewHistoryService.getViewHistories(params);
        var response = ListDataResponse.of(result.getFirst(), result.getSecond());
        return ResponseEntity.ok(new Resource<>(response));
    }
}
