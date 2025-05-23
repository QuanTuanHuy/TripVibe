package huy.project.inventory_service.ui.controller;

import huy.project.inventory_service.core.domain.dto.request.SyncUnitDto;
import huy.project.inventory_service.core.domain.entity.Unit;
import huy.project.inventory_service.core.service.IUnitService;
import huy.project.inventory_service.ui.resource.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/v1/units")
@RequiredArgsConstructor
public class UnitController {
    private final IUnitService unitService;

    @PostMapping("/sync")
    public ResponseEntity<Resource<Unit>> syncUnit(@RequestBody SyncUnitDto request) {
        return ResponseEntity.ok(new Resource<>(unitService.syncUnit(request)));
    }
}
