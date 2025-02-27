package huy.project.accommodation_service.ui.controller;

import huy.project.accommodation_service.core.domain.dto.request.CreateAmenityGroupRequestDto;
import huy.project.accommodation_service.core.domain.entity.AmenityGroupEntity;
import huy.project.accommodation_service.core.service.IAmenityGroupService;
import huy.project.accommodation_service.ui.resource.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/v1/amenity_groups")
@RequiredArgsConstructor
public class AmenityGroupController {
    private final IAmenityGroupService amenityGroupService;

    @PostMapping
    public ResponseEntity<Resource<AmenityGroupEntity>> createAmenityGroup(
            @RequestBody CreateAmenityGroupRequestDto req
    ) {
        return ResponseEntity.ok(new Resource<>(amenityGroupService.createAmenityGroup(req)));
    }
}
