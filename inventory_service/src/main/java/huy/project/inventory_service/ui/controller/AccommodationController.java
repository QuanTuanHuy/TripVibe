package huy.project.inventory_service.ui.controller;

import huy.project.inventory_service.core.domain.dto.request.SyncAccommodationDto;
import huy.project.inventory_service.core.domain.entity.Accommodation;
import huy.project.inventory_service.core.service.IAccommodationService;
import huy.project.inventory_service.ui.resource.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/v1/accommodations")
@RequiredArgsConstructor
public class AccommodationController {
    private final IAccommodationService accommodationService;

    @PostMapping
    public ResponseEntity<Resource<Accommodation>> syncAccommodation(@RequestBody SyncAccommodationDto request) {
        return ResponseEntity.ok(new Resource<>(accommodationService.syncAccommodation(request)));
    }
}
