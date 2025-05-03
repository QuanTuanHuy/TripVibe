package huy.project.accommodation_service.ui.controller.internal;

import huy.project.accommodation_service.core.domain.dto.request.AccommodationParams;
import huy.project.accommodation_service.core.domain.dto.response.AccommodationDto;
import huy.project.accommodation_service.core.domain.dto.response.UnitDto;
import huy.project.accommodation_service.core.domain.mapper.UnitMapper;
import huy.project.accommodation_service.core.service.IAccommodationService;
import huy.project.accommodation_service.core.service.IUnitService;
import huy.project.accommodation_service.ui.resource.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/internal/v1")
@RequiredArgsConstructor
public class InternalAccommodationController {
    private final IUnitService unitService;
    private final IAccommodationService accommodationService;

    @GetMapping("/units")
    public ResponseEntity<Resource<List<UnitDto>>> getUnitsByIds(
            @RequestParam(name = "unitIds") List<Long> unitIds
    ) {
        var units = unitService.getUnitsByIds(unitIds);
        var unitDtoList = units.stream().map(UnitMapper.INSTANCE::toDto).toList();
        return ResponseEntity.ok(new Resource<>(unitDtoList));
    }

    @GetMapping("/accommodations/{id}")
    public ResponseEntity<Resource<AccommodationDto>> getAccommodationById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(new Resource<>(accommodationService.getAccDtoById(id)));
    }

    @GetMapping("/accommodations")
    public ResponseEntity<Resource<List<AccommodationDto>>> getAccommodations(
            @RequestParam(name = "ids", required = false) List<Long> ids,
            @RequestParam(name = "hostId", required = false) Long hostId
    ) {
        var params = AccommodationParams.builder()
                .ids(ids)
                .hostId(hostId)
                .build();
        List<AccommodationDto> accommodations = accommodationService.getAccommodations(params).stream()
                .map(AccommodationDto::from)
                .toList();
        return ResponseEntity.ok(new Resource<>(accommodations));
    }
}
