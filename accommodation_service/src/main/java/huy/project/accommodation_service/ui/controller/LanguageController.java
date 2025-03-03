package huy.project.accommodation_service.ui.controller;

import huy.project.accommodation_service.core.domain.dto.request.CreateLanguageRequestDto;
import huy.project.accommodation_service.core.domain.dto.request.GetLanguageParams;
import huy.project.accommodation_service.core.domain.dto.response.ListDataResponse;
import huy.project.accommodation_service.core.domain.entity.LanguageEntity;
import huy.project.accommodation_service.core.service.ILanguageService;
import huy.project.accommodation_service.ui.resource.Resource;
import huy.project.accommodation_service.ui.resource.constant.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/v1/languages")
@RequiredArgsConstructor
public class LanguageController {
    private final ILanguageService languageService;

    @PostMapping
    public ResponseEntity<Resource<LanguageEntity>> createLanguage(
            @RequestBody CreateLanguageRequestDto req
    ) {
        return ResponseEntity.ok(new Resource<>(languageService.createLanguage(req)));
    }

    @GetMapping
    public ResponseEntity<Resource<ListDataResponse<LanguageEntity>>> getLanguages(
            @RequestParam(name = "page", defaultValue = Constant.DEFAULT_PAGE) Integer page,
            @RequestParam(name = "pageSize", defaultValue = Constant.DEFAULT_SIZE) Integer pageSize,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "sortBy", required = false) String sortBy,
            @RequestParam(name = "sortType", required = false) String sortType
    ) {
        GetLanguageParams params = GetLanguageParams.builder()
                .page(page).pageSize(pageSize)
                .sortBy(sortBy).sortType(sortType)
                .name(name)
                .build();
        var result = languageService.getAllLanguages(params);
        ListDataResponse<LanguageEntity> response = ListDataResponse.of(result.getFirst(), result.getSecond());
        return ResponseEntity.ok(new Resource<>(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Resource<?>> deleteLanguage(
            @PathVariable Long id
    ) {
        languageService.deleteLanguage(id);
        return ResponseEntity.ok(new Resource<>(null));
    }
}
