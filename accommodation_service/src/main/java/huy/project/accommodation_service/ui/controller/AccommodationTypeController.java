package huy.project.accommodation_service.ui.controller;

import huy.project.accommodation_service.core.domain.dto.request.CreateAccommodationTypeDto;
import huy.project.accommodation_service.core.domain.entity.AccommodationTypeEntity;
import huy.project.accommodation_service.core.service.IAccommodationTypeService;
import huy.project.accommodation_service.ui.resource.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

}
