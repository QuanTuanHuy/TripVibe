package huy.project.accommodation_service.ui.controller;

import huy.project.accommodation_service.core.domain.dto.request.CreateAccommodationDto;
import huy.project.accommodation_service.core.domain.dto.request.UpdateAccAmenityDto;
import huy.project.accommodation_service.core.domain.dto.request.UpdateUnitImageDto;
import huy.project.accommodation_service.core.domain.entity.AccommodationEntity;
import huy.project.accommodation_service.core.service.IAccommodationService;
import huy.project.accommodation_service.kernel.utils.AuthenUtils;
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

    @GetMapping("/{id}")
    public ResponseEntity<Resource<AccommodationEntity>> getDetailAccommodation(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(new Resource<>(accommodationService.getDetailAccommodation(id)));
    }

    @PostMapping("/{id}/units/{unitId}/images")
    public ResponseEntity<Resource<?>> updateUnitImage(
            @PathVariable Long id,
            @PathVariable Long unitId,
            @RequestBody UpdateUnitImageDto req
    ) {
        Long userId = AuthenUtils.getCurrentUserId();
        accommodationService.updateUnitImage(userId, id, unitId, req);
        return ResponseEntity.ok(new Resource<>(null));
    }

    @PostMapping("/{id}/amenities")
    public ResponseEntity<Resource<?>> updateAccAmenity(
            @PathVariable Long id,
            @RequestBody UpdateAccAmenityDto req
    ) {
        Long userId = AuthenUtils.getCurrentUserId();
        accommodationService.updateAccAmenity(userId, id, req);
        return ResponseEntity.ok(new Resource<>(null));
    }

}
