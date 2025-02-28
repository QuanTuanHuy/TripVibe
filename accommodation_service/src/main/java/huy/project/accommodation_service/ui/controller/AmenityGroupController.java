package huy.project.accommodation_service.ui.controller;

import huy.project.accommodation_service.core.domain.dto.request.CreateAmenityGroupRequestDto;
import huy.project.accommodation_service.core.domain.dto.request.UpdateAmenityGroupRequestDto;
import huy.project.accommodation_service.core.domain.entity.AmenityGroupEntity;
import huy.project.accommodation_service.core.service.IAmenityGroupService;
import huy.project.accommodation_service.ui.resource.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/{id}")
    public ResponseEntity<Resource<AmenityGroupEntity>> getAmenityGroupById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(new Resource<>(amenityGroupService.getAmenityGroupById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Resource<AmenityGroupEntity>> updateAmenityGroup(
            @PathVariable Long id,
            @RequestBody UpdateAmenityGroupRequestDto req
    ) {
        return ResponseEntity.ok(new Resource<>(amenityGroupService.updateAmenityGroup(id, req)));
    }

    @GetMapping
    public ResponseEntity<Resource<List<AmenityGroupEntity>>> getAllAmenityGroups() {
        return ResponseEntity.ok(new Resource<>(amenityGroupService.getAllAmenityGroups()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Resource<?>> deleteAmenityGroup(@PathVariable Long id) {
        amenityGroupService.deleteAmenityGroupById(id);
        return ResponseEntity.ok(new Resource<>(null));
    }
}
