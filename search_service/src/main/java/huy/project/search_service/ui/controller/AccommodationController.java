package huy.project.search_service.ui.controller;

import huy.project.search_service.core.domain.dto.request.AccommodationParams;
import huy.project.search_service.core.domain.entity.AccommodationEntity;
import huy.project.search_service.core.service.IAccommodationService;
import huy.project.search_service.ui.resource.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("search_service/api/public/v1/accommodations")
@RequiredArgsConstructor
@Slf4j
public class AccommodationController {
    private final IAccommodationService accommodationService;

    @PostMapping
    public ResponseEntity<Resource<AccommodationEntity>> createAccommodation(
            @RequestBody AccommodationEntity req
    ) {
        return ResponseEntity.ok(new Resource<>(accommodationService.createAccommodation(req)));
    }

    @PostMapping("/search")
    public ResponseEntity<Resource<?>> getAccommodation(
            @RequestBody AccommodationParams params
    ) {
        return ResponseEntity.ok(new Resource<>(accommodationService.getAccommodation(params)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource<AccommodationEntity>> getAccById(
            @PathVariable(name = "id") Long id
    ) {
        return ResponseEntity.ok(new Resource<>(accommodationService.getAccById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Resource<?>> deleteAccommodation(
            @PathVariable(name = "id") Long id
    ) {
        accommodationService.deleteAccommodation(id);
        return ResponseEntity.ok(new Resource<>(null));
    }
}
