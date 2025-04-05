package huy.project.accommodation_service.ui.controller;

import huy.project.accommodation_service.core.domain.dto.request.*;
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
        Long userId = AuthenUtils.getCurrentUserId();
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

    @PostMapping("/{id}/units/{unitId}/amenities")
    public ResponseEntity<Resource<?>> updateUnitAmenity(
            @PathVariable Long id,
            @PathVariable Long unitId,
            @RequestBody UpdateUnitAmenityDto req
    ) {
        Long userId = AuthenUtils.getCurrentUserId();
        accommodationService.updateUnitAmenity(userId, id, unitId, req);
        return ResponseEntity.ok(new Resource<>(null));
    }

    @PostMapping("/{id}/units")
    public ResponseEntity<Resource<?>> addUnitToAccommodation(
            @PathVariable Long id,
            @RequestBody CreateUnitDto req
    ) {
        Long userId = AuthenUtils.getCurrentUserId();
        accommodationService.addUnitToAccommodation(userId, id, req);
        return ResponseEntity.ok(new Resource<>(null));
    }

    @DeleteMapping("/{id}/units/{unitId}")
    public ResponseEntity<Resource<?>> deleteUnit(
            @PathVariable Long id,
            @PathVariable Long unitId
    ) {
        Long userId = AuthenUtils.getCurrentUserId();
        accommodationService.deleteUnit(userId, id, unitId);
        return ResponseEntity.ok(new Resource<>(null));
    }

    @PostMapping("/{id}/units/{unitId}/restore")
    public ResponseEntity<Resource<?>> restoreUnit(
            @PathVariable Long id,
            @PathVariable Long unitId
    ) {
        Long userId = AuthenUtils.getCurrentUserId();
        accommodationService.restoreUnit(userId, id, unitId);
        return ResponseEntity.ok(new Resource<>(null));
    }

    @PostMapping("/{id}/units/{unitId}/price_groups")
    public ResponseEntity<Resource<?>> updateUnitPriceGroup(
            @PathVariable Long id,
            @PathVariable Long unitId,
            @RequestBody UpdateUnitPriceGroupDto req
    ) {
        Long userId = AuthenUtils.getCurrentUserId();
        accommodationService.updateUnitPriceGroup(userId, id, unitId, req);
        return ResponseEntity.ok(new Resource<>(null));
    }

    @PostMapping("/{id}/units/{unitId}/price_calendars")
    public ResponseEntity<Resource<?>> updateUnitPriceCalendar(
            @PathVariable Long id,
            @PathVariable Long unitId,
            @RequestBody UpdateUnitPriceCalendarDto req
    ) {
        Long userId = AuthenUtils.getCurrentUserId();
        accommodationService.updateUnitPriceCalendar(userId, id, unitId, req);
        return ResponseEntity.ok(new Resource<>(null));
    }
}
