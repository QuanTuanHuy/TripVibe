package huy.project.accommodation_service.ui.controller;

import huy.project.accommodation_service.core.domain.dto.request.AccommodationTypeParams;
import huy.project.accommodation_service.core.domain.dto.request.CreateAccommodationTypeDto;
import huy.project.accommodation_service.core.domain.entity.AccommodationTypeEntity;
import huy.project.accommodation_service.core.service.IAccommodationTypeService;
import huy.project.accommodation_service.ui.resource.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/v1/accommodation_types")
@RequiredArgsConstructor
public class AccommodationTypeController {
    private final IAccommodationTypeService accommodationTypeService;

    @PostMapping
    public ResponseEntity<Resource<AccommodationTypeEntity>> createAccommodationType(
            @RequestBody CreateAccommodationTypeDto req
    ) {
        return ResponseEntity.ok(new Resource<>(accommodationTypeService.createAccommodationType(req)));
    }

    @GetMapping
    public ResponseEntity<Resource<List<AccommodationTypeEntity>>> getAccommodationTypes(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "isHighlighted", required = false) Boolean isHighlighted
    ) {
        var params = AccommodationTypeParams.builder()
                .name(name)
                .isHighlighted(isHighlighted)
                .build();
        return ResponseEntity.ok(new Resource<>(accommodationTypeService.getAccommodationTypes(params)));
    }

}
