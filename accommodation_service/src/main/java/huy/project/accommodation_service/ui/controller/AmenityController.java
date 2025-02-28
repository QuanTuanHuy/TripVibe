package huy.project.accommodation_service.ui.controller;

import huy.project.accommodation_service.core.domain.dto.request.UpdateAmenityRequestDto;
import huy.project.accommodation_service.core.domain.entity.AmenityEntity;
import huy.project.accommodation_service.core.domain.dto.request.CreateAmenityRequestDto;
import huy.project.accommodation_service.core.service.IAmenityService;
import huy.project.accommodation_service.ui.resource.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/v1/amenities")
@RequiredArgsConstructor
public class AmenityController {
    private final IAmenityService amenityService;

    @PostMapping
    public ResponseEntity<Resource<AmenityEntity>> createAmenity(
            @RequestBody CreateAmenityRequestDto req
    ) {
        return ResponseEntity.ok(new Resource<>(amenityService.createAmenity(req)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Resource<AmenityEntity>> updateAmenity(
            @PathVariable Long id,
            @RequestBody UpdateAmenityRequestDto req
    ) {
        return ResponseEntity.ok(new Resource<>(amenityService.updateAmenity(id, req)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Resource<?>> deleteAmenity(@PathVariable Long id) {
        amenityService.deleteAmenity(id);
        return ResponseEntity.ok(new Resource<>(null));
    }
}
