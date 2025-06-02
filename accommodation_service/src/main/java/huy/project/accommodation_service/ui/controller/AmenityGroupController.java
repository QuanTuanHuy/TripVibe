package huy.project.accommodation_service.ui.controller;

import huy.project.accommodation_service.core.domain.dto.request.AmenityGroupParams;
import huy.project.accommodation_service.core.domain.dto.request.CreateAmenityGroupRequestDto;
import huy.project.accommodation_service.core.domain.dto.request.UpdateAmenityGroupRequestDto;
import huy.project.accommodation_service.core.domain.dto.response.ListDataResponse;
import huy.project.accommodation_service.core.domain.entity.AmenityGroupEntity;
import huy.project.accommodation_service.core.service.IAmenityGroupService;
import huy.project.accommodation_service.ui.resource.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/v1/amenity_groups")
@RequiredArgsConstructor
public class AmenityGroupController {
    private final IAmenityGroupService amenityGroupService;

    public static final String DEFAULT_PAGE = "0";
    public static final String DEFAULT_SIZE = "10";

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
    public ResponseEntity<Resource<ListDataResponse<AmenityGroupEntity>>> getAllAmenityGroups(
            @RequestParam(name = "page", defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(name = "size", defaultValue = DEFAULT_SIZE) int pageSize,
            @RequestParam(name = "sortBy", required = false) String sortBy,
            @RequestParam(name = "sortType", required = false) String sortType,
            @RequestParam(name = "isPopular", required = false) Boolean isPopular,
            @RequestParam(name = "type", required = false) String type
    ) {
        var params = AmenityGroupParams.builder()
                .page(page).pageSize(pageSize)
                .sortBy(sortBy).sortType(sortType)
                .isPopular(isPopular)
                .type(type)
                .build();
        var result = amenityGroupService.getAllAmenityGroups(params);
        var response = ListDataResponse.of(result.getFirst(), result.getSecond());
        return ResponseEntity.ok(new Resource<>(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Resource<?>> deleteAmenityGroup(@PathVariable Long id) {
        amenityGroupService.deleteAmenityGroupById(id);
        return ResponseEntity.ok(new Resource<>(null));
    }
}
