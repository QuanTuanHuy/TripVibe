package huy.project.accommodation_service.ui.controller;

import huy.project.accommodation_service.core.domain.entity.PriceTypeEntity;
import huy.project.accommodation_service.core.service.IPriceTypeService;
import huy.project.accommodation_service.ui.resource.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/v1/price_types")
@RequiredArgsConstructor
public class PriceTypeController {
    private final IPriceTypeService priceTypeService;

    @GetMapping
    public ResponseEntity<Resource<List<PriceTypeEntity>>> getAllPriceTypes() {
        return ResponseEntity.ok(new Resource<>(priceTypeService.getALlPriceTypes()));
    }
}
