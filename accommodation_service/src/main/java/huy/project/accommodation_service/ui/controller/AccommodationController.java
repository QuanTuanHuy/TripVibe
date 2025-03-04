package huy.project.accommodation_service.ui.controller;

import huy.project.accommodation_service.core.domain.dto.request.CreateAccommodationDto;
import huy.project.accommodation_service.core.domain.entity.AccommodationEntity;
import huy.project.accommodation_service.core.service.IAccommodationService;
import huy.project.accommodation_service.ui.resource.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/v1/accommodations")
@RequiredArgsConstructor
public class AccommodationController {
    private final IAccommodationService accommodationService;

    @PostMapping
    public ResponseEntity<Resource<AccommodationEntity>> createAccommodation(
            @RequestBody CreateAccommodationDto req
    ) {
        Long userId = 1L;
        return ResponseEntity.ok(new Resource<>(accommodationService.createAccommodation(userId, req)));
    }

}
