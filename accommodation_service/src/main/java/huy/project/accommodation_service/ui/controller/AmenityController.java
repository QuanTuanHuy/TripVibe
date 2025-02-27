package huy.project.accommodation_service.ui.controller;

import huy.project.accommodation_service.core.domain.entity.AmenityEntity;
import huy.project.accommodation_service.core.domain.entity.CreateAmenityRequestDto;
import huy.project.accommodation_service.core.service.IAmenityService;
import huy.project.accommodation_service.ui.resource.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
