package huy.project.accommodation_service.ui.controller.internal;

import huy.project.accommodation_service.core.domain.dto.response.UnitDto;
import huy.project.accommodation_service.core.domain.mapper.UnitMapper;
import huy.project.accommodation_service.core.service.IUnitService;
import huy.project.accommodation_service.ui.resource.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/internal/v1")
@RequiredArgsConstructor
public class InternalAccommodationController {
    private final IUnitService unitService;

    @GetMapping("/units")
    public ResponseEntity<Resource<List<UnitDto>>> getUnitsByIds(
            @RequestParam(name = "unitIds") List<Long> unitIds
    ) {
        var units = unitService.getUnitsByIds(unitIds);
        var unitDtoList = units.stream().map(UnitMapper.INSTANCE::toDto).toList();
        return ResponseEntity.ok(new Resource<>(unitDtoList));
    }
}
