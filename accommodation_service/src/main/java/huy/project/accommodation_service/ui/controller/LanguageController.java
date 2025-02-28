package huy.project.accommodation_service.ui.controller;

import huy.project.accommodation_service.core.domain.dto.request.CreateLanguageRequestDto;
import huy.project.accommodation_service.core.domain.entity.LanguageEntity;
import huy.project.accommodation_service.core.service.ILanguageService;
import huy.project.accommodation_service.ui.resource.Resource;
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
}
