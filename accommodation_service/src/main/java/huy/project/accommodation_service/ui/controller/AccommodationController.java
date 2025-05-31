package huy.project.accommodation_service.ui.controller;

import huy.project.accommodation_service.core.domain.dto.request.*;
import huy.project.accommodation_service.core.domain.dto.response.AccommodationThumbnail;
import huy.project.accommodation_service.core.domain.entity.AccommodationEntity;
import huy.project.accommodation_service.core.service.IAccommodationService;
import huy.project.accommodation_service.kernel.utils.AuthenUtils;
import huy.project.accommodation_service.kernel.utils.JsonUtils;
import huy.project.accommodation_service.ui.resource.Resource;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/public/v1/accommodations")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class AccommodationController {
    IAccommodationService accommodationService;
    JsonUtils jsonUtils;

    @PostMapping("")
    public ResponseEntity<Resource<AccommodationEntity>> createAccommodationV2(
            @RequestPart(value = "accommodationJson") String accommodationJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        Long userId = AuthenUtils.getCurrentUserId();
        var req = jsonUtils.fromJson(accommodationJson, CreateAccommodationDtoV2.class);
        return ResponseEntity.ok(new Resource<>(accommodationService.createAccommodationV2(userId, req, images)));
    }

    @GetMapping("/thumbnails")
    public ResponseEntity<Resource<List<AccommodationThumbnail>>> getAccThumbnails(
            @RequestParam(name = "ids") List<Long> ids,
            @RequestParam(name = "startDate", required = false) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) LocalDate endDate,
            @RequestParam(name = "guestCount", required = false) Integer guestCount
    ) {
        var params = AccommodationThumbnailParams.builder()
                .ids(ids)
                .startDate(startDate)
                .endDate(endDate)
                .guestCount(guestCount)
                .build();
        return ResponseEntity.ok(new Resource<>(accommodationService.getAccommodationThumbnails(params)));
    }

    @GetMapping()
    public ResponseEntity<Resource<List<AccommodationEntity>>> getAccommodations(
            @RequestParam(name = "ids", required = false) List<Long> ids,
            @RequestParam(name = "hostId", required = false) Long hostId
    ) {
        var params = AccommodationParams.builder()
                .ids(ids)
                .hostId(hostId)
                .build();
        return ResponseEntity.ok(new Resource<>(accommodationService.getAccommodations(params)));
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
            @RequestPart(value = "requestBody", required = false) String requestBody,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        Long userId = AuthenUtils.getCurrentUserId();
        DeleteImageDto deleteImageDto = null;
        if (StringUtils.hasText(requestBody)) {
            deleteImageDto = jsonUtils.fromJson(requestBody, DeleteImageDto.class);
        }
        accommodationService.updateUnitImage(userId, id, unitId, deleteImageDto, images);
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
    public ResponseEntity<Resource<?>> addUnitToAccommodationV2(
            @PathVariable Long id,
            @RequestPart(name = "unitData") String unitDataJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        Long userId = AuthenUtils.getCurrentUserId();
        var req = jsonUtils.fromJson(unitDataJson, CreateUnitDtoV2.class);
        accommodationService.addUnitToAccommodationV2(userId, id, req, images);
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
